package net.pawjwp.scarcity.compat;

import net.minecraftforge.fml.ModList;

// Cached mod flags to check for mod presence
public final class Mods {
    public static final boolean TINKERS = ModList.get().isLoaded("tconstruct");
    public static final boolean EX_DEORUM = ModList.get().isLoaded("exdeorum");
    public static final boolean THERMAL = ModList.get().isLoaded("thermal");
}