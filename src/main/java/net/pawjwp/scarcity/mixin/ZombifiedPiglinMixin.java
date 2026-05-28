package net.pawjwp.scarcity.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.ZombifiedPiglinBurning;
import org.spongepowered.asm.mixin.Mixin;

// Set fireImmune() to false when in zombified piglin burn biomes
// Piglins already burn in the sun due when not immune due to zombie inheritance
@Mixin(ZombifiedPiglin.class)
public abstract class ZombifiedPiglinMixin extends Zombie {

    protected ZombifiedPiglinMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean fireImmune() {
        if (ZombifiedPiglinBurning.isInBurnBiome(this)) {
            return false;
        }
        return super.fireImmune();
    }
}
