package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

// Only loaded on the client if Thirst is present
public final class CamelPackClient {

    // Registers 'stored' and 'fluid' model predicates for each camel pack with a tank
    public static void registerItemProperties() {
        for (RegistryObject<CamelPackItem> pack : List.of(CamelPacks.CAMEL_PACK, CamelPacks.ADVANCED_CAMEL_PACK)) {
            ItemProperties.register(pack.get(), ResourceLocation.parse("stored"), (stack, level, entity, seed) ->
                    Mth.clamp((float) CamelPackItem.getFluid(stack).getAmount() / pack.get().getConfig().capacity().getAsInt(), 0, 1));

            ItemProperties.register(pack.get(), ResourceLocation.parse("fluid"), (stack, level, entity, seed) -> {
                FluidStack fluid = CamelPackItem.getFluid(stack);
                return fluid.isEmpty() ? 0 : CamelPackFluids.getFluidModelValue(fluid.getFluid());
            });
        }
    }
}
