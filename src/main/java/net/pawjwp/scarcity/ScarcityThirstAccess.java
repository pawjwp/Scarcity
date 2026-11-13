package net.pawjwp.scarcity;

import net.minecraft.world.entity.player.Player;

// Exposes player's thirst data methods used by the Thirst mods
public interface ScarcityThirstAccess {
    int getThirst();

    void drink(Player player, int thirst, int quenched);
}
