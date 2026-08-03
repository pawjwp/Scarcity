package net.pawjwp.scarcity.compat.thermal;

import cofh.core.common.block.EntityBlockActive4Way;
import cofh.core.util.ProxyUtils;
import cofh.core.util.helpers.AugmentDataHelper;
import cofh.thermal.core.common.config.ThermalCoreConfig;
import cofh.thermal.lib.common.item.AugmentItem;
import cofh.thermal.lib.common.item.BlockItemAugmentable;
import cofh.thermal.lib.util.ThermalAugmentRules;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.config.ScarcityConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import static cofh.lib.util.constants.NBTTags.TAG_AUGMENT_MACHINE_ENERGY;
import static cofh.lib.util.constants.NBTTags.TAG_AUGMENT_MACHINE_POWER;
import static cofh.lib.util.constants.NBTTags.TAG_AUGMENT_TYPE_MACHINE;
import static cofh.thermal.lib.util.ThermalAugmentRules.MACHINE_NO_FLUID_VALIDATOR;

// Includes the Thermal-style mechanical version of the Ex Deorum sieve and its Heavy Agitator augment.
// This class is only referenced if Thermal and Ex Deorum are present
public final class ThermalCompat {
    public static final String TAG_AUGMENT_SIEVE_HEAVY = "SieveHeavy";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Scarcity.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Scarcity.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Scarcity.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Scarcity.MOD_ID);

    public static final RegistryObject<Block> MACHINE_SIEVE = BLOCKS.register("machine_sieve",
            () -> new EntityBlockActive4Way(
                    BlockBehaviour.Properties.of().sound(SoundType.NETHERITE_BLOCK).strength(2.0F),
                    MachineSieveBlockEntity.class, () -> ThermalCompat.MACHINE_SIEVE_TILE.get()));

    public static final RegistryObject<Item> MACHINE_SIEVE_ITEM = ITEMS.register("machine_sieve",
            () -> new BlockItemAugmentable(MACHINE_SIEVE.get(), new Item.Properties())
                    .setNumSlots(() -> ThermalCoreConfig.machineAugments)
                    .setAugValidator(MACHINE_NO_FLUID_VALIDATOR)
                    .setModId(Scarcity.MOD_ID));

    public static final RegistryObject<Item> SIEVE_HEAVY_AUGMENT = ITEMS.register("sieve_heavy_augment", () -> {
        // getAugmentData reads config values on demand because config isn't loaded during item registration.
        var augment = new AugmentItem(new Item.Properties(), AugmentDataHelper.builder()
                .type(TAG_AUGMENT_TYPE_MACHINE)
                .mod(TAG_AUGMENT_SIEVE_HEAVY, 1.0F)
                .build()) {
            @Override
            public CompoundTag getAugmentData(ItemStack stack) {
                return AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_MACHINE)
                        .mod(TAG_AUGMENT_SIEVE_HEAVY, 1.0F)
                        // Thermal's power attribute is additive, so subtract 1 from the config's absolute multiplier.
                        .mod(TAG_AUGMENT_MACHINE_POWER, (float) ScarcityConfig.heavyAgitatorPowerMultiplier - 1.0F)
                        .mod(TAG_AUGMENT_MACHINE_ENERGY, (float) ScarcityConfig.heavyAgitatorEnergyMultiplier)
                        .build();
            }
        };
        ThermalAugmentRules.flagUniqueAugment(augment);
        return augment;
    });

    public static final RegistryObject<BlockEntityType<?>> MACHINE_SIEVE_TILE = BLOCK_ENTITIES.register("machine_sieve",
            () -> BlockEntityType.Builder.of(MachineSieveBlockEntity::new, MACHINE_SIEVE.get()).build(null));

    public static final RegistryObject<MenuType<MachineSieveMenu>> MACHINE_SIEVE_CONTAINER = MENUS.register("machine_sieve",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new MachineSieveMenu(windowId, ProxyUtils.getClientWorld(), data.readBlockPos(), inv, ProxyUtils.getClientPlayer())));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(ThermalCompat::onAddReloadListeners);
    }

    // Clears the mesh-recipe cache on resource reload so new recipes take effect.
    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener((prepBarrier, resourceManager, prepProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                prepBarrier.wait(Unit.INSTANCE).thenRunAsync(MachineSieveBlockEntity::clearMeshRecipeCache, gameExecutor));
    }
}