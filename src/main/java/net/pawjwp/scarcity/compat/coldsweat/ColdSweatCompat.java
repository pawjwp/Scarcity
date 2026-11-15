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
import net.pawjwp.scarcity.compat.Mods;
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
        if (Mods.CURIOS) {
            addInsulator(event, InsulationItems.STILLSUIT.get(), Insulation.Slot.CURIO, ScarcityConfig.stillsuitColdInsulation, ScarcityConfig.stillsuitHeatInsulation);
            addInsulator(event, InsulationItems.LEATHER_INSULATION.get(), Insulation.Slot.CURIO, ScarcityConfig.leatherInsulationCold, ScarcityConfig.leatherInsulationHeat);
            addInsulator(event, InsulationItems.WOOL_INSULATION.get(), Insulation.Slot.CURIO, ScarcityConfig.woolInsulationCold, ScarcityConfig.woolInsulationHeat);
        }

        addInsulator(event, InsulatingArmor.DESERT_CAP.get(), Insulation.Slot.ARMOR, ScarcityConfig.desertCapCold, ScarcityConfig.desertCapHeat);
        addInsulator(event, InsulatingArmor.DESERT_TUNIC.get(), Insulation.Slot.ARMOR, ScarcityConfig.desertTunicCold, ScarcityConfig.desertTunicHeat);
        addInsulator(event, InsulatingArmor.DESERT_PANTS.get(), Insulation.Slot.ARMOR, ScarcityConfig.desertPantsCold, ScarcityConfig.desertPantsHeat);
        addInsulator(event, InsulatingArmor.DESERT_SHOES.get(), Insulation.Slot.ARMOR, ScarcityConfig.desertShoesCold, ScarcityConfig.desertShoesHeat);
        addInsulator(event, InsulatingArmor.SNOW_HAT.get(), Insulation.Slot.ARMOR, ScarcityConfig.snowHatCold, ScarcityConfig.snowHatHeat);
        addInsulator(event, InsulatingArmor.SNOW_COAT.get(), Insulation.Slot.ARMOR, ScarcityConfig.snowCoatCold, ScarcityConfig.snowCoatHeat);
        addInsulator(event, InsulatingArmor.SNOW_PANTS.get(), Insulation.Slot.ARMOR, ScarcityConfig.snowPantsCold, ScarcityConfig.snowPantsHeat);
        addInsulator(event, InsulatingArmor.SNOW_BOOTS.get(), Insulation.Slot.ARMOR, ScarcityConfig.snowBootsCold, ScarcityConfig.snowBootsHeat);
    }

    // Adds insulation to an equipped item, based on Cold Sweat's KubeJS addInsulator
    private static void addInsulator(LoadRegistriesEvent.Pre event, Item item, Insulation.Slot slot, double cold, double heat) {
        if (cold == 0 && heat == 0) {
            return;
        }
        event.addRegistryEntry(ModRegistries.INSULATOR_DATA, new InsulatorData(
                new NegatableList<>(new ItemRequirement(List.of(item), null)), slot,
                List.of(new StaticInsulation(cold, heat)), new NegatableList<>(), new AttributeModifierMap(),
                new HashMap<>(), true, false, Optional.empty()));
    }
}
