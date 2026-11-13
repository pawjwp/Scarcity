package net.pawjwp.scarcity.compat.curios;

import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.compat.thirst.CamelPackItem;
import net.pawjwp.scarcity.compat.thirst.CamelPacks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

// Allows wearable items to work in Curios slots, only loaded if Curios is present
public final class CuriosCompat {

    // Ticks a camel pack worn in a Curios slot
    public static void registerCamelPacks() {
        ICurioItem camelPack = new ICurioItem() {
            @Override
            public void curioTick(SlotContext slotContext, ItemStack stack) {
                ((CamelPackItem) stack.getItem()).wornTick(stack, slotContext.entity());
            }
        };
        CuriosApi.registerCurio(CamelPacks.CAMEL_PACK.get(), camelPack);
        CuriosApi.registerCurio(CamelPacks.ADVANCED_CAMEL_PACK.get(), camelPack);
        CuriosApi.registerCurio(CamelPacks.UNLIMITED_CAMEL_PACK.get(), camelPack);
    }
}
