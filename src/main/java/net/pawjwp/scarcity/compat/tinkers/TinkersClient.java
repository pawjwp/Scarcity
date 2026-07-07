package net.pawjwp.scarcity.compat.tinkers;

import slimeknights.tconstruct.library.client.model.TinkerItemProperties;

// Only loaded if Tinkers Construct is present
public final class TinkersClient {
    public static void registerItemProperties() {
        TinkerItemProperties.registerToolProperties(TinkersCompat.CROOK.get());
        TinkerItemProperties.registerToolProperties(TinkersCompat.CRUSHING_HAMMER.get());
    }
}