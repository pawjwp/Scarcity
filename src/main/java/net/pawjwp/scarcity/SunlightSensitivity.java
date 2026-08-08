package net.pawjwp.scarcity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

// Functions to control sunlight sensitivity for mobs based on the ScarcitySunlightSensitivity NBT tag.
// The tag is a boolean stored at the root of the mob's data, read/set elsewhere.
// When the tag is absent, defaults to vanilla's behavior
// When the tag is true, the mob loses fire immunity and burns in the daylight
// When the tag if false, the mob does not burn in the daylight, even if it normally would
public final class SunlightSensitivity {

    public static final String TAG = "ScarcitySunlightSensitivity";

    public static State getState(Mob mob) {
        return ((ScarcitySunlightAccess) mob).scarcity$sunlightState();
    }

    public enum State {
        DEFAULT, SENSITIVE, INSENSITIVE;

        private static final State[] VALUES = values();

        public static State byId(int id) {
            return VALUES[id];
        }
    }

    private SunlightSensitivity() {}

    // Recreation of vanilla Mob#isSunBurnTick to allow Scarcity to control daylight burning
    public static boolean isSunBurnTick(Mob mob) {
        if (!mob.level().isDay() || mob.level().isClientSide) {
            return false;
        }
        float brightness = mob.getLightLevelDependentMagicValue();
        BlockPos eyePos = BlockPos.containing(mob.getX(), mob.getEyeY(), mob.getZ());
        boolean wet = mob.isInWaterRainOrBubble() || mob.isInPowderSnow || mob.wasInPowderSnow;
        return brightness > 0.5F
                && mob.getRandom().nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && !wet
                && mob.level().canSeeSky(eyePos);
    }

    // Recreation of vanilla's daylight burning, including immunity from wearing a helmet
    public static void applySunBurn(Mob mob) {
        ItemStack helmet = mob.getItemBySlot(EquipmentSlot.HEAD);
        if (!helmet.isEmpty()) {
            if (helmet.isDamageableItem()) {
                helmet.setDamageValue(helmet.getDamageValue() + mob.getRandom().nextInt(2));
                if (helmet.getDamageValue() >= helmet.getMaxDamage()) {
                    mob.broadcastBreakEvent(EquipmentSlot.HEAD);
                    mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }
            return;
        }
        mob.setSecondsOnFire(8);
    }
}