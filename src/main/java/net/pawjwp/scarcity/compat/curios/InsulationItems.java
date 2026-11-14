package net.pawjwp.scarcity.compat.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.Mods;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Registers insulation slot items, only run if Curios is present
public final class InsulationItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Scarcity.MOD_ID);

    public static final RegistryObject<Item> STILLSUIT = ITEMS.register("stillsuit", () -> new Item(new Item.Properties().stacksTo(1)) {
        // Show a tooltip of what the stillsuit does if the relevant mod is installed
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag) {
            if (Mods.COLD_SWEAT) {
                text.add(Component.translatable("item.scarcity.stillsuit.insulation").withStyle(ChatFormatting.GRAY));
            }
            if (Mods.THIRST) {
                text.add(Component.translatable("item.scarcity.stillsuit.moisture").withStyle(ChatFormatting.GRAY));
            }
        }
    });

    // Only register if Cold Sweat is installed
    public static final RegistryObject<Item> LEATHER_INSULATION;
    public static final RegistryObject<Item> WOOL_INSULATION;

    static {
        if (Mods.COLD_SWEAT) {
            LEATHER_INSULATION = ITEMS.register("leather_insulation", () -> new Item(new Item.Properties().stacksTo(1)));
            WOOL_INSULATION = ITEMS.register("wool_insulation", () -> new Item(new Item.Properties().stacksTo(1)));
        } else {
            LEATHER_INSULATION = null;
            WOOL_INSULATION = null;
        }
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
