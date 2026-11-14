package net.pawjwp.scarcity.mixin;

import net.minecraft.world.entity.player.Player;
import net.pawjwp.scarcity.ScarcityThirstAccess;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.curios.CuriosCompat;
import net.pawjwp.scarcity.compat.curios.InsulationItems;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Target either Thirst was Taken or Thirst was Reclaimed
@Pseudo
@Mixin(targets = {"dev.ghen.thirst.content.thirst.PlayerThirst", "cn.mlus.thirst.content.thirst.PlayerThirst"}, remap = false)
public abstract class PlayerThirstMixin implements ScarcityThirstAccess {

    // Scales thirst exhaustion by the configured value
    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true, require = 0)
    private float scarcity$stillsuitExhaustion(float amount, Player player) {
        if (amount <= 0 || !Mods.CURIOS || !CuriosCompat.isWearing(player, InsulationItems.STILLSUIT.get())) {
            return amount;
        }
        return amount * (float) ScarcityConfig.stillsuitThirstMultiplier;
    }
}
