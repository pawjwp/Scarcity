package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;

public class ScarcityModifiers extends AbstractModifierProvider {
    public ScarcityModifiers(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addModifiers() {
        // Add modifier, functionality is handled in Ex Deorum mixins
        buildModifier(TinkersCompat.HEAVY_HAMMERING).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    }

    @Override
    public String getName() {
        return "Scarcity Modifiers";
    }
}