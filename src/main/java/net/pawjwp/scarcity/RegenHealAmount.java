package net.pawjwp.scarcity;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;

// Sets the amount that natural regeneration heals, run from FoodDataMixin
public class RegenHealAmount {

    public static Player player;

    public static float amount;

    public static void onHeal(LivingHealEvent event) {
        if (event.getEntity() == player) {
            event.setAmount(amount);
        }
    }
}
