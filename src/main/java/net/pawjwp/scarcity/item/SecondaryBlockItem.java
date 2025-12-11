package net.pawjwp.scarcity.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class SecondaryBlockItem extends BlockItem {

    public SecondaryBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {

    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}
