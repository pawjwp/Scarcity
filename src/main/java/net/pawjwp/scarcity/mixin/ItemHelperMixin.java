package net.pawjwp.scarcity.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Forces CoFH's ItemHelper to use ItemStack.getMaxStackSize() instead of stack.getItem().getMaxStackSize(stack).
// This allows Thermal series mods to properly recognize the stack size of items after the stack size has been changed by other mods.
@Pseudo
@Mixin(targets = "cofh.core.util.helpers.ItemHelper", remap = false)
public class ItemHelperMixin {

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
        return stack.getMaxStackSize();
    }
}
