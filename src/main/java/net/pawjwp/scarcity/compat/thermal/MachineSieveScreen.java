package net.pawjwp.scarcity.compat.thermal;

import cofh.thermal.core.client.gui.ThermalGuiHelper;
import cofh.thermal.lib.client.gui.MachineScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.pawjwp.scarcity.Scarcity;

import static cofh.core.util.helpers.GuiHelper.*;

public class MachineSieveScreen extends MachineScreen<MachineSieveMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "textures/gui/container/machine_sieve.png");

    // The progress arrow doubles as the JEI recipe click area
    public static final int PROGRESS_X = 52;
    public static final int PROGRESS_Y = 47;

    public MachineSieveScreen(MachineSieveMenu container, Inventory inv, Component titleIn) {
        super(container, inv, container.tile, titleIn);
        texture = TEXTURE;
        info = generatePanelInfo("info.scarcity.machine_sieve");
        name = "sieve";
        imageHeight = 190;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void init() {
        super.init();

        // input and mesh slots
        addElement(createInputSlot(this, MachineSieveMenu.INPUT_X, MachineSieveMenu.INPUT_Y, tile));
        addElement(createInputSlot(this, MachineSieveMenu.INPUT_X, MachineSieveMenu.MESH_Y, tile));

        // output slot grid
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                addElement(createOutputSlot(this, MachineSieveMenu.OUTPUT_X + col * 18, MachineSieveMenu.OUTPUT_Y + row * 18, tile));
            }
        }

        // progress indicator
        addElement(ThermalGuiHelper.createDefaultProgress(this, PROGRESS_X, PROGRESS_Y, PROG_ARROW_RIGHT, tile));
        addElement(ThermalGuiHelper.createDefaultSpeed(this, MachineSieveMenu.INPUT_X, 47, SCALE_COMPACT, tile));
    }
}
