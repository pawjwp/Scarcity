package net.pawjwp.scarcity.datagen.loot;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.pawjwp.scarcity.compat.thermal.ThermalCompat;

import java.util.List;
import java.util.Set;

public class ScarcityBlockLootTables extends BlockLootSubProvider {
    public ScarcityBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ThermalCompat.MACHINE_SIEVE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(ThermalCompat.MACHINE_SIEVE.get());
    }
}
