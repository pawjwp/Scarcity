package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;
// Wearable pack that allows the wearer to drink liquids from its tank automatically and hands-free
public class CamelPackItem extends Item {
    private static final String cdKey = "ScarcityCamelPackCD"; // name of cooldown in NBT

    private final CamelPackTier config;

    public CamelPackItem(CamelPackTier config) {
        super(new Item.Properties().stacksTo(1));
        this.config = config;
    }

    public CamelPackTier getConfig() {
        return config;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return config.hasTank() ? new FluidHandlerItemStack(stack, config.capacity().getAsInt()) : null;
    }
}
