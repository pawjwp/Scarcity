package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_fixes to be enabled in config.
// Affects Tinkers' Construct tools that pick up fluids with their tank
@Pseudo 
@Mixin(targets = "slimeknights.tconstruct.tools.modules.interaction.BucketModule", remap = false)   
public class BucketModuleMixin {

    // Allow water with purity to be picked up by tinkers tank tools
    @WrapOperation(
            method = "onToolUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/FluidStack;hasTag()Z"
            ),
            require = 0
    )
    private boolean scarcity$ignorePurityTag(FluidStack fluid, Operation<Boolean> original) {
        if (!ScarcityConfig.enableThirstPurityFixes) {
            return original.call(fluid);
        }

        FluidStack withoutPurity = ThirstCompat.withoutPurity(fluid);
        return withoutPurity.hasTag() && !withoutPurity.getTag().isEmpty();
    }

    // Reads the purity of the water before picking it up
    @WrapOperation(
            method = "onToolUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;",
                    remap = true
            ),
            require = 0
    )
    private ItemStack scarcity$readSourcePurity(BucketPickup pickup, LevelAccessor level, BlockPos pos, BlockState state, Operation<ItemStack> original, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityFixes && level instanceof Level sourceLevel) {
            purity.set(ThirstCompat.getSourcePurity(sourceLevel, pos));
        }
        return original.call(pickup, level, pos, state);
    }

    // Adds the water's purity to the water stored in an empty tool (based on how Thirst handles vanilla buckets)
    @ModifyArg(
            method = "onToolUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lslimeknights/tconstruct/library/tools/capability/fluid/ToolTankHelper;setFluid(Lslimeknights/tconstruct/library/tools/nbt/IToolStackView;Lnet/minecraftforge/fluids/FluidStack;)Lnet/minecraftforge/fluids/FluidStack;",
                    ordinal = 0
            ),
            index = 1,
            require = 0
    )
    private FluidStack scarcity$addSourcePurity(FluidStack fluid, @Share("purity") LocalRef<Integer> purity) {
        if (purity.get() != null) {
            ThirstCompat.addPurity(fluid, purity.get());
        }
        return fluid;
    }

    // Merges the picked up water with the water already stored in the tool
    // Requires enable_thirst_purity_merging to be enabled in config
    @ModifyArg(
            method = "onToolUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lslimeknights/tconstruct/library/tools/capability/fluid/ToolTankHelper;setFluid(Lslimeknights/tconstruct/library/tools/nbt/IToolStackView;Lnet/minecraftforge/fluids/FluidStack;)Lnet/minecraftforge/fluids/FluidStack;",
                    ordinal = 1
            ),
            index = 1,
            require = 0
    )
    private FluidStack scarcity$mergePurity(FluidStack stored, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityMerging && purity.get() != null) {
            FluidStack incoming = new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME);
            ThirstCompat.addPurity(incoming, purity.get());
            ThirstCompat.mergePurity(stored, incoming, FluidType.BUCKET_VOLUME);
        }
        return stored;
    }
}
