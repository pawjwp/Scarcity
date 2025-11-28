package net.pawjwp.scarcity.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;

public class ScarcityCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Scarcity.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SCARCITY_TAB = CREATIVE_MODE_TABS.register("scarcity_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ScarcityItems.OAK_SEED.get()))
            .title(Component.translatable("creativetab.scarcity_tab"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ScarcityItems.OAK_SEED.get());
                // List more items for the creative tab as needed
            })
            .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
