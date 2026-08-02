package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;

// Injects Scarcity's modifiers into Tinkers' book sections. Membership in a book tag makes the
// modifier auto-generate a page (Puny Smelting harvest upgrades and the Encyclopedia of Tinkering).
public class ScarcityModifierTags extends AbstractModifierTagProvider {
    public ScarcityModifierTags(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Scarcity.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(TinkerTags.Modifiers.HARVEST_UPGRADES).add(TinkersCompat.HEAVY_HAMMERING);
    }

    @Override
    public String getName() {
        return "Scarcity Modifier Tags";
    }
}
