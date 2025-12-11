package net.pawjwp.scarcity.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
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
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }
    public static RegistryObject<Item> registerSeed(String name, ResourceLocation blockID) {
        RegistryObject<Item> item = ITEMS.register(name, () -> {
            var block = ForgeRegistries.BLOCKS.getValue(blockID);

            if (block == null || block == Blocks.AIR) {
                return new Item(new Item.Properties());
            }

            return new SecondaryBlockItem(block, new Item.Properties());
        });

        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }


    // Item registry
    public static final RegistryObject<Item> OAK_SEED = registerSeed("oak_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.OAK_SAPLING));
    public static final RegistryObject<Item> SPRUCE_SEED = registerSeed("spruce_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.SPRUCE_SAPLING));
    public static final RegistryObject<Item> BIRCH_SEED = registerSeed("birch_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.BIRCH_SAPLING));
    public static final RegistryObject<Item> JUNGLE_SEED = registerSeed("jungle_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.JUNGLE_SAPLING));
    public static final RegistryObject<Item> ACACIA_SEED = registerSeed("acacia_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.ACACIA_SAPLING));
    public static final RegistryObject<Item> DARK_OAK_SEED = registerSeed("dark_oak_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.DARK_OAK_SAPLING));
    public static final RegistryObject<Item> CHERRY_SEED = registerSeed("cherry_seed",
            ForgeRegistries.BLOCKS.getKey(Blocks.CHERRY_SAPLING));

    public static final RegistryObject<Item> BAMBOO_SEEDS = registerSeed("bamboo_seeds",
            ForgeRegistries.BLOCKS.getKey(Blocks.BAMBOO));

    public static final RegistryObject<Item> CACTUS_SEEDS = registerSeed("cactus_seeds",
            ForgeRegistries.BLOCKS.getKey(Blocks.CACTUS));
    public static final RegistryObject<Item> SUGAR_CANE_SEEDS = registerSeed("sugar_cane_seeds",
            ForgeRegistries.BLOCKS.getKey(Blocks.SUGAR_CANE));
    public static final RegistryObject<Item> SWEET_BERRY_SEEDS = registerSeed("sweet_berry_seeds",
            ForgeRegistries.BLOCKS.getKey(Blocks.SWEET_BERRY_BUSH));

    public static final RegistryObject<Item> POTATO_SEEDS = registerSeed("potato_seeds",
            ForgeRegistries.BLOCKS.getKey(Blocks.POTATOES));
    public static final RegistryObject<Item> CARROT_SEEDS = registerSeed("carrot_seeds",
            ForgeRegistries.BLOCKS.getKey(Blocks.CARROTS));

    public static final RegistryObject<Item> RUBBERWOOD_SEED = registerSeed("rubberwood_seed",
            ResourceLocation.parse("thermal:rubberwood_sapling"));
    public static final RegistryObject<Item> ONION_SEEDS = registerSeed("onion_seeds",
            ResourceLocation.parse("farmersdelight:onions"));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
