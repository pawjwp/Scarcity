package net.pawjwp.scarcity.mixin;

import net.pawjwp.scarcity.ScarcityThirstAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

// Target either Thirst was Taken or Thirst was Reclaimed
@Pseudo
@Mixin(targets = {"dev.ghen.thirst.content.thirst.PlayerThirst", "cn.mlus.thirst.content.thirst.PlayerThirst"}, remap = false)
public abstract class PlayerThirstMixin implements ScarcityThirstAccess {
}
