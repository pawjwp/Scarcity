package net.pawjwp.scarcity.compat.thermal;

import cofh.core.client.event.CoreClientEvents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.pawjwp.scarcity.Scarcity;

// This class is only referenced if Thermal and Ex Deorum are present
public final class ThermalClient {
    public static void registerScreens() {
        MenuScreens.register(ThermalCompat.MACHINE_SIEVE_CONTAINER.get(), MachineSieveScreen::new);
        // Registers the Scarcity namespace for CoFH's item description tooltips (.desc lang keys)
        CoreClientEvents.addNamespace(Scarcity.MOD_ID);
    }
}