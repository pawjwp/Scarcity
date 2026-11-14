package net.pawjwp.scarcity.compat.coldsweat;

import com.momosoftworks.coldsweat.api.event.core.registry.LoadRegistriesEvent;
import com.momosoftworks.coldsweat.api.insulation.Insulation;
import com.momosoftworks.coldsweat.api.insulation.StaticInsulation;
import com.momosoftworks.coldsweat.data.ModRegistries;
import com.momosoftworks.coldsweat.data.codec.configuration.InsulatorData;
import com.momosoftworks.coldsweat.data.codec.requirement.ItemRequirement;
import com.momosoftworks.coldsweat.data.codec.util.AttributeModifierMap;
import com.momosoftworks.coldsweat.data.codec.util.NegatableList;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.pawjwp.scarcity.compat.curios.InsulationItems;
import net.pawjwp.scarcity.config.ScarcityConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

// Apply Cold Sweat stats to items
public final class ColdSweatCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ColdSweatCompat::registerInsulators);
    }

    // Run whenever Cold Sweat data is loaded
    private static void registerInsulators(LoadRegistriesEvent.Pre event) {
        addInsulator(event, InsulationItems.STILLSUIT.get(), ScarcityConfig.stillsuitColdInsulation, ScarcityConfig.stillsuitHeatInsulation);
        addInsulator(event, InsulationItems.LEATHER_INSULATION.get(), ScarcityConfig.leatherInsulationCold, ScarcityConfig.leatherInsulationHeat);
        addInsulator(event, InsulationItems.WOOL_INSULATION.get(), ScarcityConfig.woolInsulationCold, ScarcityConfig.woolInsulationHeat);
    }

    // Adds insulation to an item in a Curios slot, based on Cold Sweat's KubeJS addInsulator
    private static void addInsulator(LoadRegistriesEvent.Pre event, Item item, double cold, double heat) {
        if (cold == 0 && heat == 0) {
            return;
        }
        event.addRegistryEntry(ModRegistries.INSULATOR_DATA, new InsulatorData(
                new NegatableList<>(new ItemRequirement(List.of(item), null)), Insulation.Slot.CURIO,
                List.of(new StaticInsulation(cold, heat)), new NegatableList<>(), new AttributeModifierMap(),
                new HashMap<>(), true, false, Optional.empty()));
    }
}
