package net.pawjwp.scarcity.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pawjwp.scarcity.Scarcity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ScarcityBlockTags extends BlockTagsProvider {
    public ScarcityBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Scarcity.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Optionally registers tags when mods are present
        tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "machine_sieve"));
    }
}