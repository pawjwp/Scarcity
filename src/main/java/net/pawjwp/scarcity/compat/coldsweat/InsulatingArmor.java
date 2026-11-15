package net.pawjwp.scarcity.compat.coldsweat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// Desert and snow armor, only registered if Cold Sweat is present
public final class InsulatingArmor {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Scarcity.MOD_ID);

    public static final RegistryObject<Item> DESERT_CAP = register("desert_cap", Material.DESERT, ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> DESERT_TUNIC = register("desert_tunic", Material.DESERT, ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> DESERT_PANTS = register("desert_pants", Material.DESERT, ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> DESERT_SHOES = register("desert_shoes", Material.DESERT, ArmorItem.Type.BOOTS);

    public static final RegistryObject<Item> SNOW_HAT = register("snow_hat", Material.SNOW, ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> SNOW_COAT = register("snow_coat", Material.SNOW, ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> SNOW_PANTS = register("snow_pants", Material.SNOW, ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> SNOW_BOOTS = register("snow_boots", Material.SNOW, ArmorItem.Type.BOOTS);

    private static RegistryObject<Item> register(String name, Material material, ArmorItem.Type type) {
        // Tooltips
        String tooltip = "item.scarcity." + material.name + "_armor";
        return ITEMS.register(name, () -> new ArmorItem(material, type, new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag) {
                text.add(Component.translatable(tooltip + ".description").withStyle(ChatFormatting.GRAY));
                text.add(Component.translatable(tooltip + ".effect").withStyle(ChatFormatting.GRAY));
            }
        });
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private enum Material implements ArmorMaterial {
        DESERT("desert", () -> Ingredient.of(Tags.Items.LEATHER)),
        SNOW("snow", () -> Ingredient.of(ItemTags.WOOL));

        // Durability per piece (multiplied by 12 in getDurabilityForType)
        private static final Map<ArmorItem.Type, Integer> DURABILITY = Map.of(
                ArmorItem.Type.HELMET, 11,
                ArmorItem.Type.CHESTPLATE, 16,
                ArmorItem.Type.LEGGINGS, 15,
                ArmorItem.Type.BOOTS, 13
        );
        // Protection per piece, the same as leather armor
        private static final Map<ArmorItem.Type, Integer> PROTECTION = Map.of(
                ArmorItem.Type.HELMET, 1,
                ArmorItem.Type.CHESTPLATE, 3,
                ArmorItem.Type.LEGGINGS, 2,
                ArmorItem.Type.BOOTS, 1
        );

        private final String name;
        private final Supplier<Ingredient> repairIngredient;

        Material(String name, Supplier<Ingredient> repairIngredient) {
            this.name = name;
            this.repairIngredient = repairIngredient;
        }

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return DURABILITY.get(type) * 12;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return PROTECTION.get(type);
        }

        @Override
        public int getEnchantmentValue() {
            return 5;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_GENERIC;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairIngredient.get();
        }

        @Override
        public String getName() {
            return Scarcity.MOD_ID + ":" + name;
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }
}
