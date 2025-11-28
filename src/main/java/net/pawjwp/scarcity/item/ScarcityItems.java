package net.pawjwp.scarcity.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class ScarcityItems {
    public static LinkedHashSet<RegistryObject<Item>> CREATIVE_TAB_ITEMS = new LinkedHashSet<>();;

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Scarcity.MOD_ID);

    public static RegistryObject<Item> registerWithTab(String name, Supplier<Item> supplier) {
        RegistryObject<Item> block = ITEMS.register(name, supplier);
        CREATIVE_TAB_ITEMS.add(block);
        return block;
    }

    // Item registry
    public static final RegistryObject<Item> OAK_SEED = registerWithTab("oak_seed",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPRUCE_SEED = registerWithTab("spruce_seed",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BIRCH_SEED = registerWithTab("birch_seed",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> JUNGLE_SEED = registerWithTab("jungle_seed",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ACACIA_SEED = registerWithTab("acacia_seed",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DARK_OAK_SEED = registerWithTab("dark_oak_seed",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHERRY_SEED = registerWithTab("cherry_seed",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
