package net.pawjwp.scarcity.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.pawjwp.scarcity.compat.thirst.CamelPackItem;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;

// Crafting recipe for a camel pack combined with a container of water to fill the camel pack
public class CamelPackFillRecipe extends CustomRecipe {
    public static final int BOTTLE_AMOUNT = 250;

    public CamelPackFillRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    // The filled camel pack, with the container slot of the fluid container and the item left in that slot
    private record Filled(ItemStack camelPack, int containerSlot, ItemStack leftovers) {
    }

    // Fills the camel pack in the crafting grid
    private static Filled fill(CraftingContainer container) {
        // The grid must have one camel pack and one other item
        int camelPackSlot = -1;
        int containerSlot = -1;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            // camel pack must have a tank
            if (camelPackSlot < 0 && stack.getItem() instanceof CamelPackItem item && item.getConfig().hasTank()) {
                camelPackSlot = slot;
            } else if (containerSlot < 0) {
                containerSlot = slot;
            } else {
                return null;
            }
        }
        if (camelPackSlot < 0 || containerSlot < 0) {
            return null;
        }

        ItemStack camelPack = container.getItem(camelPackSlot).copy();
        ItemStack fluidContainer = container.getItem(containerSlot).copyWithCount(1);

        // Read current camel pack fluid state
        FluidStack currentFluid = CamelPackItem.getFluid(camelPack);
        int capacity = ((CamelPackItem) camelPack.getItem()).getConfig().capacity().getAsInt();

        // Purity of the fluid container, read before the container is emptied
        int containerPurity = ThirstCompat.getPurity(fluidContainer);

        // Remove fluid from the fluid container
        FluidStack fluid;
        ItemStack leftovers;
        if (fluidContainer.is(Items.POTION) && PotionUtils.getPotion(fluidContainer) == Potions.WATER) {
            fluid = new FluidStack(Fluids.WATER, BOTTLE_AMOUNT);
            leftovers = new ItemStack(Items.GLASS_BOTTLE);
        } else {
            IFluidHandlerItem containerTank = FluidUtil.getFluidHandler(fluidContainer).orElse(null);
            if (containerTank == null) {
                return null;
            }
            fluid = containerTank.drain(capacity - currentFluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
            leftovers = containerTank.getContainer();
        }
        if (fluid.isEmpty()) {
            return null;
        }

        // Null if the camel pack has a different fluid
        if (!currentFluid.isEmpty() && !currentFluid.getFluid().isSame(fluid.getFluid())) {
            return null;
        }

        int newAmount = currentFluid.getAmount() + fluid.getAmount();
        if (newAmount > capacity) {
            return null;
        }

        // Merge water purity using Scarcity's merging system, storing it in the same place a Forge fluid tank does
        if (fluid.getFluid().isSame(Fluids.WATER)) {
            int purity = fluid.hasTag() && fluid.getTag().contains("Purity") ? ThirstCompat.getPurity(fluid) : containerPurity;
            if (!currentFluid.isEmpty()) {
                purity = ThirstCompat.mergedPurity(ThirstCompat.getPurity(currentFluid), purity, fluid.getAmount(), newAmount);
            }
            ThirstCompat.addPurity(fluid, purity);
        }

        fluid.setAmount(newAmount);
        camelPack.getOrCreateTag().put(FluidHandlerItemStack.FLUID_NBT_KEY, fluid.writeToNBT(new CompoundTag()));
        return new Filled(camelPack, containerSlot, leftovers);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return fill(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return fill(container).camelPack;
    }

    // Leaves the emptied fluid container in the grid and nothing else, since the camel pack is the crafting result
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        Filled filled = fill(container);
        remaining.set(filled.containerSlot, filled.leftovers);
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScarcityRecipeSerializers.CAMEL_PACK_FILL.get();
    }
}
