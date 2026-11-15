package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.pawjwp.scarcity.ScarcityThirstAccess;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

// Wearable pack that allows the wearer to drink liquids from its tank automatically and hands-free
public class CamelPackItem extends Item {
    public static final int BOTTLE_AMOUNT = 250; // liquid held by one water bottle
    private static final String cdKey = "ScarcityCamelPackCD"; // name of cooldown in NBT

    private final CamelPackTier config;

    public CamelPackItem(CamelPackTier config) {
        super(new Item.Properties().stacksTo(1));
        this.config = config;
    }

    // The fluid stored in a camel pack, which is empty for a pack with no tank
    public static FluidStack getFluid(ItemStack stack) {
        return FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
    }

    public CamelPackTier getConfig() {
        return config;
    }

    // Moves fluid from one fluid container into a camel pack
    // Returns the item left after emptying the fluid container, or null if the fluid can't be moved
    public static ItemStack fill(ItemStack camelPack, ItemStack fluidContainer) {
        // camel pack must have a tank
        if (!(camelPack.getItem() instanceof CamelPackItem item) || !item.config.hasTank()) {
            return null;
        }
        fluidContainer = fluidContainer.copyWithCount(1);

        // Read current camel pack fluid state
        FluidStack currentFluid = getFluid(camelPack);
        int capacity = item.config.capacity().getAsInt();

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
        return leftovers;
    }

    // Moves fluid from a camel pack into a fluid container
    public static ItemStack drain(ItemStack camelPack, ItemStack fluidContainer) {
        IFluidHandlerItem packTank = FluidUtil.getFluidHandler(camelPack).orElse(null);
        FluidStack fluid = getFluid(camelPack);
        if (packTank == null || fluid.isEmpty()) {
            return null;
        }
        fluidContainer = fluidContainer.copyWithCount(1);

        // Add fluid to the fluid container
        int moved;
        ItemStack filled;
        if (fluidContainer.is(Items.GLASS_BOTTLE)) {
            if (!fluid.getFluid().isSame(Fluids.WATER) || fluid.getAmount() < BOTTLE_AMOUNT) {
                return null;
            }
            moved = BOTTLE_AMOUNT;
            filled = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        } else {
            IFluidHandlerItem containerTank = FluidUtil.getFluidHandler(fluidContainer).orElse(null);
            if (containerTank == null) {
                return null;
            }
            moved = containerTank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            filled = containerTank.getContainer();
        }
        if (moved == 0) {
            return null;
        }

        packTank.drain(moved, IFluidHandler.FluidAction.EXECUTE);
        return ThirstCompat.withFluidPurity(filled, fluid);
    }

    // Fills or drains the camel pack from a fluid container right-clicked in the inventory
    private static boolean transferByClick(ItemStack camelPack, ItemStack fluidContainer, Player player, Consumer<ItemStack> replaceContainer) {
        ItemStack result = fill(camelPack, fluidContainer);
        boolean filling = result != null;
        if (!filling) {
            result = drain(camelPack, fluidContainer);
        }
        if (result == null) {
            return false;
        }
        // Use as many fluid containers from the stack as are possible to insert
        while (result != null) {
            if (fluidContainer.getCount() == 1) {
                replaceContainer.accept(result);
                break;
            }
            fluidContainer.shrink(1);
            player.getInventory().placeItemBackInInventory(result);
            result = filling ? fill(camelPack, fluidContainer) : drain(camelPack, fluidContainer);
        }
        player.playSound(filling ? SoundEvents.BOTTLE_EMPTY : SoundEvents.BOTTLE_FILL, 0.8F, 1.0F);
        return true;
    }

    // Fills or drains the camel pack when it is right-clicked onto a fluid container
    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        return action == ClickAction.SECONDARY && slot.allowModification(player)
                && transferByClick(stack, slot.getItem(), player, slot::set);
    }

    // Fills or drains the camel pack when a fluid container is right-clicked onto it
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return action == ClickAction.SECONDARY && slot.allowModification(player)
                && transferByClick(stack, other, player, access::set);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return config.hasTank() ? new FluidHandlerItemStack(stack, config.capacity().getAsInt()) : null;
    }

    // Runs every tick that the pack is worn
    public void wornTick(ItemStack stack, LivingEntity entity) {
        // run on server for player
        if (entity.level().isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        // cooldown
        CompoundTag data = player.getPersistentData();
        int cd = data.getInt(cdKey);
        if (cd > 0) {
            data.putInt(cdKey, cd - 1);
            return;
        }

        // thirst cap
        ScarcityThirstAccess thirstCap = ThirstCompat.getThirst(player);
        if (thirstCap == null) {
            return;
        }

        int sipSize = ScarcityConfig.camelPackSipAmount; // liquid drained per sip
        double sipThirst = ScarcityConfig.camelPackBottleThirst * sipSize / 250; // base thirst restored per sip, calculated from water bottle restoration amount
        double sipQuenched = ScarcityConfig.camelPackBottleQuenched * sipSize / 250; // base quenchness restored per sip, calculated from water bottle restoration amount
        double thirstMult = config.thirstMult().getAsDouble();
        double quenchMult = config.quenchMult().getAsDouble();

        // if tankless, drink separately
        if (!config.hasTank()) {
            if (thirstCap.getThirst() > 18) {
                return;
            }
            thirstCap.drink(player,
                    (int) (sipThirst * thirstMult),
                    (int) (sipQuenched * quenchMult)
            );
            data.putInt(cdKey, 20);
            return;
        }

        // read the stored fluid
        IFluidHandlerItem tank = FluidUtil.getFluidHandler(stack).orElse(null);
        FluidStack fluid = getFluid(stack);
        if (tank == null || fluid.isEmpty()) {
            return;
        }

        // fluids consume remaining amounts even if under the sipSize
        int drinkAmount = Math.min(sipSize, fluid.getAmount());
        double sipRatio = (double) drinkAmount / sipSize;

        switch (ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).toString()) {
            case "minecraft:water" -> {
                // if thirst is greater than 18, do not drink
                if (thirstCap.getThirst() > 18) {
                    return;
                }

                tank.drain(drinkAmount, IFluidHandler.FluidAction.EXECUTE);
                int drinkPurity = Math.min(ThirstCompat.getPurity(fluid) + config.purityBonus().getAsInt(), ThirstCompat.MAX_PURITY);

                // roll Thirst's purity effects on a portion of sips equal to the sip size over a 250 mB bottle
                // no thirst is granted if the player gets poisoned
                if (player.getRandom().nextDouble() >= drinkAmount / 250.0
                        || ThirstCompat.givePurityEffects(player, drinkPurity)) {
                    thirstCap.drink(player,
                            (int) (sipThirst * thirstMult * sipRatio),
                            (int) (sipQuenched * quenchMult * sipRatio)
                    );
                }

                // set 20 tick cooldown
                data.putInt(cdKey, 20);
            }
            case "minecraft:milk" -> {
                // if thirst is greater than 18, do not drink
                if (thirstCap.getThirst() > 18) {
                    return;
                }

                tank.drain(drinkAmount, IFluidHandler.FluidAction.EXECUTE);
                thirstCap.drink(player,
                        (int) (sipThirst * (2.0 / 3) * thirstMult * sipRatio),
                        (int) (sipQuenched * (2.0 / 3) * quenchMult * sipRatio)
                );

                // increase food by 1, saturation by 1
                FoodData food = player.getFoodData();
                food.setFoodLevel(Math.min(food.getFoodLevel() + 1, 20));
                food.setSaturation(Math.min(food.getSaturationLevel() + 1.0F, food.getFoodLevel()));
                data.putInt(cdKey, 20);
            }
            case "minecraft:lava" -> {
                tank.drain(drinkAmount, IFluidHandler.FluidAction.EXECUTE);
                // deal 2 fire damage, light player on fire for 2 seconds
                player.hurt(player.damageSources().onFire(), 2.0F);
                player.setSecondsOnFire(2);
                data.putInt(cdKey, 20);
            }
            default -> {
            }
        }
    }

    // Lets the pack be worn in the chestplate slot when Curios is not installed
    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return Mods.CURIOS ? null : EquipmentSlot.CHEST;
    }

    // Ticks the pack worn in the chestplate slot, since Curios ticks it instead when installed
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!Mods.CURIOS && entity instanceof Player player && player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
            wornTick(stack, player);
        }
    }

    // durability bar based on tank amount
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !getFluid(stack).isEmpty(); // hide bar if empty
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float) getFluid(stack).getAmount() / config.capacity().getAsInt() * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return CamelPackClient.barColor(getFluid(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag) {
        if (config.hasTank()) {
            FluidStack fluid = getFluid(stack);
            if (fluid.isEmpty()) {
                text.add(Component.translatable("item.scarcity.camel_pack.empty"));
            } else {
                text.add(Component.translatable("item.scarcity.camel_pack.fluid", fluid.getDisplayName()));
                // show water purity tooltip matching Thirst's purity text and style
                if (fluid.getFluid().isSame(Fluids.WATER)) {
                    text.add(ThirstCompat.getPurityText(ThirstCompat.getPurity(fluid)));
                }
                text.add(Component.translatable("item.scarcity.camel_pack.amount", fluid.getAmount(), config.capacity().getAsInt()));
            }
        }
        text.add(Component.translatable(getDescriptionId() + ".description").withStyle(ChatFormatting.GRAY));
    }
}
