package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @ModifyExpressionValue(
            method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BlockItem;getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState scarcity$preventWaterlogInFlowingWater(BlockState original, @Local BlockPlaceContext context) {
        if (!ScarcityConfig.enableWaterPlantSourcePrevention) return original;
        if (original == null) return original;
        if (!original.hasProperty(BlockStateProperties.WATERLOGGED)) return original;
        if (!original.getValue(BlockStateProperties.WATERLOGGED)) return original;
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        if (fluid.is(FluidTags.WATER) && !fluid.isSource()) {
            return original.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        return original;
    }
}
