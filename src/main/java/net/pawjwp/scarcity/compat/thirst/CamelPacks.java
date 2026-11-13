package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.config.ScarcityConfig;

// Registers each camel pack tier (if Thirst is present)
public final class CamelPacks {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Scarcity.MOD_ID);

    public static final RegistryObject<CamelPackItem> CAMEL_PACK = registerCamelPack(new CamelPackTier("camel_pack", true,
            () -> ScarcityConfig.camelPackCapacity,
            () -> ScarcityConfig.camelPackThirstMultiplier,
            () -> ScarcityConfig.camelPackQuenchMultiplier,
            () -> ScarcityConfig.camelPackPurityBonus));

    public static final RegistryObject<CamelPackItem> ADVANCED_CAMEL_PACK = registerCamelPack(new CamelPackTier("advanced_camel_pack", true,
            () -> ScarcityConfig.advancedCamelPackCapacity,
            () -> ScarcityConfig.advancedCamelPackThirstMultiplier,
            () -> ScarcityConfig.advancedCamelPackQuenchMultiplier,
            () -> ScarcityConfig.advancedCamelPackPurityBonus));

    public static final RegistryObject<CamelPackItem> UNLIMITED_CAMEL_PACK = registerCamelPack(new CamelPackTier("unlimited_camel_pack", false,
            () -> 0,
            () -> ScarcityConfig.unlimitedCamelPackThirstMultiplier,
            () -> ScarcityConfig.unlimitedCamelPackQuenchMultiplier,
            () -> 0));

    private static RegistryObject<CamelPackItem> registerCamelPack(CamelPackTier config) {
        return ITEMS.register(config.id(), () -> new CamelPackItem(config));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
