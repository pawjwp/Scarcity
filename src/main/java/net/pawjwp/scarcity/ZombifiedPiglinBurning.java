package net.pawjwp.scarcity;

import net.minecraft.world.entity.Entity;
import net.pawjwp.scarcity.config.ScarcityConfig;

// Check for whether a zombified piglin should lose its fire immunity at its current position.
// Used by the vanilla and SpecialMobs mixins.
public final class ZombifiedPiglinBurning {

    private ZombifiedPiglinBurning() {}

    public static boolean isInBurnBiome(Entity entity) {
        if (ScarcityConfig.zombifiedPiglinBurnBiomes.isEmpty()) {
            return false;
        }
        return entity.level().getBiome(entity.blockPosition()).unwrapKey()
                .map(key -> ScarcityConfig.zombifiedPiglinBurnBiomes.contains(key.location()))
                .orElse(false);
    }
}
