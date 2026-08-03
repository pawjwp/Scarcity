package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.pawjwp.scarcity.datagen.loot.ScarcityBlockLootTables;

import java.util.List;
import java.util.Set;

public class ScarcityLootTables {
    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ScarcityBlockLootTables::new, LootContextParamSets.BLOCK)
        ));
    }
}
