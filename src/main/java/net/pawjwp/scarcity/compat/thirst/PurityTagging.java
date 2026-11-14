package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.pawjwp.scarcity.config.ScarcityConfig;
import net.pawjwp.scarcity.tag.ScarcityTags;

// Add a purity tag to water without one when it enters a player's inventory
// Applies to all items in the scarcity:force_purity tag, and defaults to configured untagged water purity
public record PurityTagging(Player player) implements ContainerListener {

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> listen(event.getEntity().inventoryMenu, event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> listen(event.getEntity().inventoryMenu, event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((PlayerContainerEvent.Open event) -> {
            if (!(event.getContainer() instanceof InventoryMenu)) {
                listen(event.getContainer(), event.getEntity());
            }
        });
    }

    private static void listen(AbstractContainerMenu menu, Player player) {
        menu.addSlotListener(new PurityTagging(player));
    }

    @Override
    public void slotChanged(AbstractContainerMenu menu, int index, ItemStack changed) {
        if (!ScarcityConfig.forceThirstPurityTagging || menu.getSlot(index).container != player.getInventory()) {
            return;
        }

        ItemStack stack = menu.getSlot(index).getItem();
        if (stack.is(ScarcityTags.FORCE_PURITY) && !(stack.hasTag() && stack.getTag().contains("Purity"))
                // A potion only counts if it is a water bottle
                && (!stack.is(Items.POTION) || PotionUtils.getPotion(stack) == Potions.WATER)) {
            ThirstCompat.addPurity(stack, ThirstCompat.getPurity(stack));
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu menu, int id, int value) {
    }
}
