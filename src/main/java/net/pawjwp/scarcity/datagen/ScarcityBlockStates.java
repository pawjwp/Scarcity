package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.thermal.ThermalCompat;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.ACTIVE;
import static cofh.lib.util.constants.BlockStatePropertiesCoFH.FACING_HORIZONTAL;

public class ScarcityBlockStates extends BlockStateProvider {
    public ScarcityBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Scarcity.MOD_ID, exFileHelper);
    }

    private String blockName(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "block/" + path);
    }

    @Override
    protected void registerStatesAndModels() {
        this.machineBlock(ThermalCompat.MACHINE_SIEVE.get());
    }

    // Uses Thermal's machine base model, using a custom front texture (and a different texture when active)
    public void machineBlock(Block block) {
        String name = blockName(block);
        ModelFile idle = models().getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile(thermalBlock("machine_base")))
                .texture("north", resourceBlock(name + "_front"));
        ModelFile active = models().getBuilder(name + "_active")
                .parent(new ModelFile.UncheckedModelFile(thermalBlock("machine_base")))
                .texture("north", resourceBlock(name + "_front_active"));

        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(ACTIVE) ? active : idle)
                .rotationY(((int) state.getValue(FACING_HORIZONTAL).toYRot() + 180) % 360)
                .build());
    }

    private ResourceLocation thermalBlock(String path) {
        return ResourceLocation.fromNamespaceAndPath("thermal", "block/" + path);
    }
}
