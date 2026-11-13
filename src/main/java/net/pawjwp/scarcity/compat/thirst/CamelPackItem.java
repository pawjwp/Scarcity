package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

// Wearable pack that allows the wearer to drink liquids from its tank automatically and hands-free
public class CamelPackItem extends Item {
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
}
