package net.pawjwp.scarcity.compat.thermal;

import cofh.lib.common.inventory.ItemStorageCoFH;
import cofh.thermal.core.common.config.ThermalCoreConfig;
import cofh.thermal.lib.common.block.entity.MachineBlockEntity;
import cofh.thermal.lib.util.recipes.IMachineInventory;
import cofh.thermal.lib.util.recipes.internal.IMachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.pawjwp.scarcity.config.ScarcityConfig;
import thedarkcolour.exdeorum.recipe.RecipeUtil;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;
import thedarkcolour.exdeorum.tag.EItemTags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cofh.core.util.helpers.AugmentableHelper.getAttributeMod;
import static cofh.core.util.helpers.ItemHelper.itemsEqualWithTags;
import static cofh.lib.api.StorageGroup.*;

// The Particulate Sieve is a Thermal-style machine that uses Ex Deorum's sieve recipes
public class MachineSieveBlockEntity extends MachineBlockEntity {
    public static final int OUTPUT_SLOTS = 20;

    protected ItemStorageCoFH inputSlot = new ItemStorageCoFH(item -> filter.valid(item) && hasRecipeForAnyMesh(item));
    protected ItemStorageCoFH meshSlot = new ItemStorageCoFH(1, item -> item.is(EItemTags.SIEVE_MESHES));

    // The Heavy Agitator augment allows the sieve to sift compressed blocks
    protected boolean heavyAgitator = false;

    public MachineSieveBlockEntity(BlockPos pos, BlockState state) {
        super(ThermalCompat.MACHINE_SIEVE_TILE.get(), pos, state);

        inventory.addSlot(inputSlot, INPUT);
        inventory.addSlot(meshSlot, CATALYST);
        inventory.addSlots(OUTPUT, OUTPUT_SLOTS);
        inventory.addSlot(chargeSlot, INTERNAL);

        addAugmentSlots(ThermalCoreConfig.machineAugments);
        initHandlers();
    }

    // Whether any mesh has a sieve recipe for a given item. Cleared on resource reload.
    private static final Map<Item, Boolean> MESH_RECIPE_CACHE = new HashMap<>();

    static void clearMeshRecipeCache() {
        MESH_RECIPE_CACHE.clear();
    }

    // Checks if an item has recipes for a mesh type so that item can be inserted in the input slot
    private static boolean hasRecipeForAnyMesh(ItemStack stack) {
        return MESH_RECIPE_CACHE.computeIfAbsent(stack.getItem(), item -> {
            for (var mesh : ForgeRegistries.ITEMS.tags().getTag(EItemTags.SIEVE_MESHES)) {
                if (!RecipeUtil.getSieveRecipes(mesh, stack).isEmpty()
                        || !RecipeUtil.getCompressedSieveRecipes(mesh, stack).isEmpty()) {
                    return true;
                }
            }
            return false;
        });
    }

    private static List<SieveRecipe> sieveRecipes(ItemStack mesh, ItemStack input, boolean includeCompressed) {
        List<SieveRecipe> recipes = new ArrayList<>(RecipeUtil.getSieveRecipes(mesh.getItem(), input));
        if (includeCompressed) {
            recipes.addAll(RecipeUtil.getCompressedSieveRecipes(mesh.getItem(), input));
        }
        recipes.removeIf(recipe -> recipe.byHandOnly);
        return recipes;
    }

    protected int getMeshFortune() {
        return meshSlot.getItemStack().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
    }

    protected int getMeshEfficiency() {
        return meshSlot.getItemStack().getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
    }

    @Override
    protected int getBaseProcessTick() {
        return ScarcityConfig.machineSieveEnergyPerTick;
    }

    @Override
    protected boolean cacheRecipe() {
        ItemStack input = inputSlot.getItemStack();
        ItemStack mesh = meshSlot.getItemStack();
        if (!ScarcityConfig.enableMachineSieve || input.isEmpty() || mesh.isEmpty()) {
            return false;
        }
        // Keep the same recipe while inputs are unchanged
        if (!(curRecipe instanceof SieveMachineRecipe recipe) || !recipe.matches(input, mesh, heavyAgitator)) {
            curRecipe = SieveMachineRecipe.create(input, mesh, heavyAgitator);
        }
        if (curRecipe != null) {
            itemInputCounts = curRecipe.getInputItemCounts(this);
        }
        return curRecipe != null;
    }

    @Override
    protected boolean validateInputs() {
        return cacheRecipe() && inputSlot.getCount() >= itemInputCounts.get(0);
    }

    // Allows the operation if at least one output item fits. Items that don't fit are discarded in resolveOutputs.
    @Override
    protected boolean validateOutputs() {
        if (curRecipe == null && !cacheRecipe()) {
            return false;
        }
        List<ItemStack> outputs = curRecipe.getOutputItems(this);
        if (outputs.isEmpty()) {
            return true;
        }
        for (ItemStack output : outputs) {
            for (ItemStorageCoFH slot : outputSlots()) {
                ItemStack existing = slot.getItemStack();
                if (existing.isEmpty() || (itemsEqualWithTags(existing, output) && existing.getCount() < existing.getMaxStackSize())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Distributes output items across output slots. Items that don't fit are discarded.
    @Override
    protected void resolveOutputs() {
        for (ItemStack output : curRecipe.getOutputItems(this)) {
            int remaining = output.getCount();
            for (ItemStorageCoFH slot : outputSlots()) {
                ItemStack existing = slot.getItemStack();
                if (existing.isEmpty()) {
                    int moved = Math.min(remaining, output.getMaxStackSize());
                    ItemStack placed = output.copy();
                    placed.setCount(moved);
                    slot.setItemStack(placed);
                    remaining -= moved;
                } else if (itemsEqualWithTags(existing, output) && existing.getCount() < existing.getMaxStackSize()) {
                    int moved = Math.min(remaining, existing.getMaxStackSize() - existing.getCount());
                    existing.grow(moved);
                    remaining -= moved;
                }
                if (remaining <= 0) {
                    break;
                }
            }
        }
    }

    @Override
    protected void resolveInputs() {
        inputSlot.consume(itemInputCounts.get(0));
        // Clears the cached output so the next operation generates new drops.
        if (curRecipe instanceof SieveMachineRecipe recipe) {
            recipe.clearRoll();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new MachineSieveMenu(i, level, worldPosition, inventory, player);
    }

    // region AUGMENTS
    @Override
    protected void resetAttributes() {
        super.resetAttributes();
        heavyAgitator = false;
    }

    @Override
    protected void setAttributesFromAugment(CompoundTag augmentData) {
        super.setAttributesFromAugment(augmentData);
        heavyAgitator |= getAttributeMod(augmentData, ThermalCompat.TAG_AUGMENT_SIEVE_HEAVY) > 0;
    }

    @Override
    protected void finalizeAttributes(Map<Enchantment, Integer> enchantmentMap) {
        boolean wasHeavy = heavyAgitator;
        super.finalizeAttributes(enchantmentMap);
        // Changing the agitator mid-operation stops processing.
        if (wasHeavy != heavyAgitator && level != null && !level.isClientSide && isActive) {
            processOff();
        }
    }
    // endregion

    // Wraps Ex Deorum sieve recipes as an IMachineRecipe. 
    // Output items are generated once per operation so validateOutputs and resolveOutputs see the same items.
    protected static class SieveMachineRecipe implements IMachineRecipe {
        private final Item input;
        private final Item mesh;
        private final boolean withCompressed;
        private final List<SieveRecipe> recipes;
        @Nullable
        private List<ItemStack> roll;

        private SieveMachineRecipe(Item input, Item mesh, boolean withCompressed, List<SieveRecipe> recipes) {
            this.input = input;
            this.mesh = mesh;
            this.withCompressed = withCompressed;
            this.recipes = recipes;
        }

        @Nullable
        static SieveMachineRecipe create(ItemStack input, ItemStack mesh, boolean withCompressed) {
            var recipes = sieveRecipes(mesh, input, withCompressed);
            return recipes.isEmpty() ? null : new SieveMachineRecipe(input.getItem(), mesh.getItem(), withCompressed, recipes);
        }

        boolean matches(ItemStack input, ItemStack mesh, boolean withCompressed) {
            return this.input == input.getItem() && this.mesh == mesh.getItem() && this.withCompressed == withCompressed;
        }

        void clearRoll() {
            this.roll = null;
        }

        @Override
        public List<ItemStack> getOutputItems(IMachineInventory inventory) {
            if (this.roll == null) {
                var machine = (MachineSieveBlockEntity) inventory;
                var ctx = RecipeUtil.emptyLootContext((ServerLevel) machine.getLevel());
                var rand = ctx.getRandom();
                int fortune = machine.getMeshFortune();

                List<ItemStack> rolled = new ArrayList<>();
                // TODO: Ex Deorum's limitMossSieveDrops config implementation
                //  In practice, it rarely limits anything because most affected recipes use single-roll amounts.
                for (SieveRecipe recipe : recipes) {
                    int count = recipe.resultAmount.getInt(ctx);
                    // Each level of fortune on the mesh grants a 30% chance for an extra roll, like Ex Deorum's sieves
                    for (int i = 0; i < fortune; i++) {
                        if (rand.nextFloat() < 0.3f) {
                            count += recipe.resultAmount.getInt(ctx);
                        }
                    }
                    if (count > 0) {
                        ItemStack result = new ItemStack(recipe.result, count);
                        result.setTag(recipe.getResultNbt());
                        rolled.add(result);
                    }
                }
                this.roll = rolled;
            }
            return this.roll;
        }

        @Override
        public List<Float> getOutputItemChances(IMachineInventory inventory) {
            return Collections.nCopies(this.roll == null ? 0 : this.roll.size(), 1.0F);
        }

        @Override
        public List<ItemStack> getInputItems() {
            return List.of(new ItemStack(input));
        }

        @Override
        public List<Integer> getInputItemCounts(IMachineInventory inventory) {
            return List.of(1);
        }

        @Override
        public List<FluidStack> getInputFluids() {
            return List.of();
        }

        @Override
        public List<FluidStack> getOutputFluids(IMachineInventory inventory) {
            return List.of();
        }

        @Override
        public List<Integer> getInputFluidCounts(IMachineInventory inventory) {
            return List.of();
        }

        @Override
        public int getEnergy(IMachineInventory inventory) {
            var machine = (MachineSieveBlockEntity) inventory;
            // Efficiency reduces energy per operation by 17% per level, speeding up sifting at the same RF/t.
            float meshSpeed = 1.0f + 0.17f * machine.getMeshEfficiency();
            return Math.round(ScarcityConfig.machineSieveEnergyPerOperation * inventory.getMachineProperties().getEnergyMod() / meshSpeed);
        }

        @Override
        public float getXp(IMachineInventory inventory) {
            return 0;
        }
    }
}
