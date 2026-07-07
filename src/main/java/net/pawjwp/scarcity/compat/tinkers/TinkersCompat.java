package net.pawjwp.scarcity.compat.tinkers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.item.ScarcityItems;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

// Tinkers Construct versions of the Ex Deorum crook and hammer
// Only loaded if Tinkers Construct is present
public final class TinkersCompat {
    // Modifier that makes a crushing hamemr behave like a compressed hammer
    public static final ModifierId HEAVY_HAMMERING = new ModifierId(Scarcity.MOD_ID, "heavy_hammering");

    public static final ToolDefinition CROOK_DEFINITION = ToolDefinition.create(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "crook"));
    public static final ToolDefinition CRUSHING_HAMMER_DEFINITION = ToolDefinition.create(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "crushing_hammer"));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Scarcity.MOD_ID);

    public static final RegistryObject<Item> CROOK = ITEMS.register("crook",
            () -> new ModifiableItem(new Item.Properties().stacksTo(1), CROOK_DEFINITION));
    public static final RegistryObject<Item> CRUSHING_HAMMER = ITEMS.register("crushing_hammer",
            () -> new ModifiableItem(new Item.Properties().stacksTo(1), CRUSHING_HAMMER_DEFINITION));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ScarcityItems.CREATIVE_TAB_ITEMS.add(CROOK);
        ScarcityItems.CREATIVE_TAB_ITEMS.add(CRUSHING_HAMMER);
    }

    public static boolean isBroken(ItemStack stack) {
        return ToolDamageUtil.isBroken(stack);
    }

    public static boolean isCrushingHammer(ItemStack stack) {
        return stack.is(CRUSHING_HAMMER.get());
    }

    public static boolean hasHeavyHammering(ItemStack stack) {
        return ModifierUtil.getModifierLevel(stack, HEAVY_HAMMERING) > 0;
    }

    public static boolean isTinkersTool(ItemStack stack) {
        return stack.is(TinkerTags.Items.MODIFIABLE);
    }
    public static void damageTool(ItemStack stack) {
        ToolStack tool = ToolStack.from(stack);
        if (!tool.isBroken()) {
            ToolDamageUtil.damage(tool, 1, null, stack);
        }
    }
}