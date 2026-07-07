package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.ToolActions;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.definition.module.aoe.BoxAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.IBoxExpansion;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class ScarcityToolDefinitions extends AbstractToolDefinitionDataProvider {
    public ScarcityToolDefinitions(PackOutput packOutput) {
        super(packOutput, Scarcity.MOD_ID);
    }

    @Override
    protected void addToolDefinitions() {
        RandomMaterial tier1Material = RandomMaterial.random().tier(1).build();
        DefaultMaterialsModule defaultThreeParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material).build();

        define(TinkersCompat.CROOK_DEFINITION)
                .module(PartStatsModule.parts()
                        .part(TinkerToolParts.adzeHead)
                        .part(TinkerToolParts.toolHandle, 0.5f)
                        .part(TinkerToolParts.toolHandle, 0.5f).build())
                .module(defaultThreeParts)
                .module(new SetStatsModule(StatsNBT.builder()
                        .set(ToolStats.ATTACK_DAMAGE, 0.75f)
                        .set(ToolStats.ATTACK_SPEED, 1.0f)
                        .set(ToolStats.DURABILITY, 1.0f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                        .set(ToolStats.DURABILITY, 2.0f).build()))
                .smallToolStartingSlots()
                .module(ToolTraitsModule.builder().trait(new ModifierId("tconstruct", "padded"), 2).build())
                .module(ToolActionsModule.of(ToolActions.HOE_DIG))
                .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_HOE));

        define(TinkersCompat.CRUSHING_HAMMER_DEFINITION)
                .module(PartStatsModule.parts()
                        .part(TinkerToolParts.adzeHead)
                        .part(TinkerToolParts.toolHandle)
                        .part(TinkerToolParts.toolBinding).build())
                .module(defaultThreeParts)
                .module(new SetStatsModule(StatsNBT.builder()
                        .set(ToolStats.ATTACK_DAMAGE, 1.0f)
                        .set(ToolStats.ATTACK_SPEED, 0.8f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                        .set(ToolStats.DURABILITY, 1.25f).build()))
                .smallToolStartingSlots()
                .module(ToolTraitsModule.builder().trait(ModifierIds.smite, 1).build())
                .module(ToolActionsModule.of(ToolActions.PICKAXE_DIG))
                .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
                .module(BoxAOEIterator.builder(0, 0, 0).addDepth(2).addHeight(1).direction(IBoxExpansion.PITCH).build());
    }

    @Override
    public String getName() {
        return "Scarcity Tool Definitions";
    }
}