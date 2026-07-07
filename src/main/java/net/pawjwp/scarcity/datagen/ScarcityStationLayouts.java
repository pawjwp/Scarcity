package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class ScarcityStationLayouts extends AbstractStationSlotLayoutProvider {
    public ScarcityStationLayouts(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        defineModifiable((IModifiableDisplay) TinkersCompat.CROOK.get())
                .sortIndex(2)
                .addInputItem(TinkerToolParts.adzeHead, 48, 26)
                .addInputItem(TinkerToolParts.toolHandle, 12, 62)
                .addInputItem(TinkerToolParts.toolHandle, 30, 44)
                .build();

        defineModifiable((IModifiableDisplay) TinkersCompat.CRUSHING_HAMMER.get())
                .sortIndex(2)
                .addInputItem(TinkerToolParts.adzeHead, 48, 26)
                .addInputItem(TinkerToolParts.toolHandle, 12, 62)
                .addInputItem(TinkerToolParts.toolBinding, 30, 44)
                .build();
    }

    @Override
    public String getName() {
        return "Scarcity Station Slot Layouts";
    }
}