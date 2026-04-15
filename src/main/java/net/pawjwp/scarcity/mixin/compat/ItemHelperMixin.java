package net.pawjwp.scarcity.mixin.compat;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Requires enable_thermal_patches to be enabled in config.
@Pseudo
@Mixin(targets = "cofh.core.util.helpers.ItemHelper", remap = false)
public class ItemHelperMixin {

    // Forces ItemHelper to use ItemStack.getMaxStackSize() instead of Item.getMaxStackSize(ItemStack).
    // This allows Thermal machines to properly recognize modified stack sizes from other mods.
    @Redirect(
            method = "consumeItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I",
                    remap = false
            ),
            require = 0
    )
    private static int scarcity$useStackMaxSize(Item item, ItemStack stack) {
        if (ScarcityConfig.enableThermalPatches) {
            return stack.getMaxStackSize();
        }
        return item.getMaxStackSize(stack);
    }

    // Prevents crafting remainder items from being left in the input slot after processing.
    // Makes Thermal machines behave like vanilla furnaces by fully consuming the input item.
    @Redirect(
            method = "consumeItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;hasCraftingRemainingItem(Lnet/minecraft/world/item/ItemStack;)Z",
                    remap = false
            ),
            require = 0
    )
    private static boolean scarcity$disableCraftingRemainder(Item item, ItemStack stack) {
        if (ScarcityConfig.enableThermalPatches) {
            return false;
        }
        return item.hasCraftingRemainingItem(stack);
    }
}