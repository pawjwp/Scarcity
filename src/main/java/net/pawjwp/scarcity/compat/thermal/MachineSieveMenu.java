package net.pawjwp.scarcity.compat.thermal;

import cofh.core.common.inventory.BlockEntityCoFHMenu;
import cofh.lib.common.inventory.SlotCoFH;
import cofh.lib.common.inventory.SlotRemoveOnly;
import cofh.lib.common.inventory.wrapper.InvWrapperCoFH;
import cofh.thermal.lib.common.block.entity.Reconfigurable4WayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// 176x190 layout to fit the 5x4 output grid.
public class MachineSieveMenu extends BlockEntityCoFHMenu {
    // Slot coordinates, also used by MachineSieveScreen for the visual slot elements.
    public static final int INPUT_X = 32;   // left column: input slot + mesh slot
    public static final int INPUT_Y = 29;   // top input slot
    public static final int MESH_Y = 65;    // mesh slot
    public static final int OUTPUT_X = 80;  // top-left of the 5x4 output grid
    public static final int OUTPUT_Y = 20;
    public static final int CHARGE_X = 8;   // energy charge slot
    public static final int CHARGE_Y = 53;

    public final Reconfigurable4WayBlockEntity tile;

    public MachineSieveMenu(int windowId, Level level, BlockPos pos, Inventory inventory, Player player) {
        super(ThermalCompat.MACHINE_SIEVE_CONTAINER.get(), windowId, level, pos, inventory, player);
        this.tile = (Reconfigurable4WayBlockEntity) level.getBlockEntity(pos);
        InvWrapperCoFH tileInv = new InvWrapperCoFH(this.tile.getItemInv());

        // input and mesh slots
        addSlot(new SlotCoFH(tileInv, 0, INPUT_X, INPUT_Y));
        addSlot(new SlotCoFH(tileInv, 1, INPUT_X, MESH_Y));

        // output slot grid
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                addSlot(new SlotRemoveOnly(tileInv, 2 + row * 5 + col, OUTPUT_X + col * 18, OUTPUT_Y + row * 18));
            }
        }

        // charge slot
        addSlot(new SlotCoFH(tileInv, 22, CHARGE_X, CHARGE_Y));

        bindAugmentSlots(tileInv, 23, this.tile.augSize());
        bindPlayerInventory(inventory);
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {
        return 108;
    }
}
