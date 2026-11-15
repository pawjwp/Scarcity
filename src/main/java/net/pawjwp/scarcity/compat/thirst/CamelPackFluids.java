package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Maps a list of fluid IDs to their model predicate value and durability bar color
public record CamelPackFluids(float modelValue, int barColor, List<String> fluids) {
    private static final List<CamelPackFluids> FLUID_CONFIG = List.of(
            // Blue
            new CamelPackFluids(0.01F, ChatFormatting.BLUE.getColor(), List.of(
                    "minecraft:water",
                    "mekanism:heavy_water"
            )),
            // Orange
            new CamelPackFluids(0.02F, DyeColor.ORANGE.getTextColor(), List.of(
                    "minecraft:lava",
                    "thermal:syrup",
                    "thermal:sap",
                    "thermal:light_oil",
                    "tconstruct:magma",
                    "tconstruct:ichor",
                    "tconstruct:blazing_blood",
                    "pneumaticcraft:diesel",
                    "create:tea"
            )),
            // White
            new CamelPackFluids(0.03F, ChatFormatting.WHITE.getColor(), List.of(
                    "minecraft:milk",
                    "thermal:latex",
                    "tconstruct:venom",
                    "tconstruct:powdered_snow",
                    "pneumaticcraft:yeast_culture",
                    "pneumaticcraft:plastic",
                    "pneumaticcraft:ethanol",
                    "industrialforegoing:latex",
                    "industrialforegoing:ether_gas",
                    "biggerreactors:steam",
                    "ad_astra:oxygen"
            )),
            // Lime
            new CamelPackFluids(0.04F, DyeColor.LIME.getTextColor(), List.of(
                    "sophisticatedcore:xp_still",
                    "pneumaticcraft:memory_essence",
                    "mob_grinding_utils:fluid_xp",
                    "industrialforegoing:essence",
                    "enderio:xp_juice",
                    "cofh_core:experience"
            )),
            // Purple
            new CamelPackFluids(0.05F, DyeColor.PURPLE.getTextColor(), List.of(
                    "tconstruct:ender_slime",
                    "industrialforegoing:sludge",
                    "industrialforegoing:biofuel",
                    "exdeorum:witch_water"
            )),
            // Brown
            new CamelPackFluids(0.06F, DyeColor.BROWN.getTextColor(), List.of(
                    "thermal:tree_oil",
                    "tconstruct:rabbit_stew",
                    "tconstruct:mushroom_stew",
                    "tconstruct:meat_soup",
                    "tconstruct:liquid_soul",
                    "industrialforegoing:sewage",
                    "industrialforegoing:meat",
                    "create:chocolate"
            )),
            // Yellow
            new CamelPackFluids(0.07F, ChatFormatting.GOLD.getColor(), List.of(
                    "thermal:resin",
                    "thermal:refined_fuel",
                    "thermal:glowstone",
                    "tconstruct:honey",
                    "pneumaticcraft:vegetable_oil",
                    "pneumaticcraft:lubricant",
                    "pneumaticcraft:lpg",
                    "pneumaticcraft:gasoline",
                    "pneumaticcraft:biodiesel",
                    "mekanism:chlorine",
                    "mekanism:brine",
                    "createaddition:bioethanol",
                    "create:honey",
                    "cofh_core:honey"
            )),
            // Red
            new CamelPackFluids(0.08F, ChatFormatting.RED.getColor(), List.of(
                    "thermal:redstone",
                    "thermal:heavy_oil",
                    "tconstruct:beetroot_soup",
                    "ad_astra:fuel"
            )),
            // Teal
            new CamelPackFluids(0.09F, ChatFormatting.DARK_AQUA.getColor(), List.of(
                    "thermal:ender",
                    "tconstruct:molten_ender"
            )),
            // Cyan
            new CamelPackFluids(0.10F, ChatFormatting.AQUA.getColor(), List.of(
                    "tconstruct:sky_slime",
                    "pneumaticcraft:kerosene",
                    "ad_astra:hydrogen",
                    "ad_astra:cryo_fuel"
            )),
            // Green
            new CamelPackFluids(0.11F, ChatFormatting.GREEN.getColor(), List.of(
                    "tconstruct:earth_slime",
                    "pneumaticcraft:etching_acid",
                    "biggerreactors:liquid_uranium"
            )),
            // Pink
            new CamelPackFluids(0.12F, DyeColor.PINK.getTextColor(), List.of(
                    "industrialforegoing:pink_slime"
            )),
            // Black/Gray (Default)
            new CamelPackFluids(0.00F, ChatFormatting.GRAY.getColor(), List.of(
                    "thermal:crude_oil",
                    "thermal:creosote",
                    "tconstruct:seared_stone",
                    "tconstruct:scorched_stone",
                    "pneumaticcraft:oil",
                    "biggerreactors:liquid_obsidian",
                    "ad_astra:oil"
            ))
    );

    // Build maps of fluid IDs
    private static final Map<String, CamelPackFluids> FLUID_LOOKUP = new HashMap<>();
    static {
        FLUID_CONFIG.forEach(entry -> entry.fluids.forEach(fluidId -> FLUID_LOOKUP.put(fluidId, entry)));
    }

    public static int getFluidBarColor(Fluid fluidType) {
        CamelPackFluids entry = FLUID_LOOKUP.get(ForgeRegistries.FLUIDS.getKey(fluidType).toString());
        return entry != null ? entry.barColor : ChatFormatting.GRAY.getColor();
    }
}
