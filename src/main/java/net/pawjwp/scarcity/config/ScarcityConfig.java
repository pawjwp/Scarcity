package net.pawjwp.scarcity.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.pawjwp.scarcity.BurnRule;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

// reference: https://cadiboo.github.io/tutorials/1.15.2/forge/3.3-config/

public class ScarcityConfig {

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    // fast regen
    public static int fastRegenFoodThreshold;
    public static int fastRegenIntervalTicks;
    public static float fastRegenSaturationCap;

    // slow regen
    public static int slowRegenFoodThreshold;
    public static int slowRegenIntervalTicks;
    public static float slowRegenHealAmount;

    // exhaustion
    public static float exhaustionStep;
    public static float passiveExhaustionPerTick;
    public static float negativeExhaustionThreshold;

    // exhaustion – jump
    public static float exhaustionJumpSprinting;
    public static float exhaustionJumpWalking;

    // exhaustion – combat
    public static float exhaustionAttackHit;
    public static float exhaustionDamageMultiplier;

    // exhaustion – movement
    public static float exhaustionWalkOneBlock;
    public static float exhaustionSprintOneBlock;
    public static float exhaustionSwimOneBlock;
    public static float exhaustionWalkUnderwaterOneBlock;
    public static float exhaustionWalkOnWaterOneBlock;
    public static float exhaustionClimbOneBlock;
    public static float exhaustionCrouchOneBlock;
    public static float exhaustionAviateOneBlock;
    public static float exhaustionFlyOneBlock;

    // misc tweaks
    public static boolean enableBottlePickupAdjustments;
    public static boolean enableFallingBlockBreakingAdjustments;
    public static boolean enableZombieVillagerCuring;
    public static List<BurnRule> zombifiedPiglinBurnRules = List.of();
    public static boolean enableWaterPlantSourcePrevention;

    // thermal patches
    public static boolean enableThermalPatches;

    // obscure api patches
    public static boolean hideObscureAPIMenuButton;

    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            bakeConfig();
        }
    }

    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            bakeConfig();
        }
    }

    private static List<BurnRule> parseBurnRules(List<? extends UnmodifiableConfig> raw) {
        return raw.stream().map(BurnRule::fromConfig).collect(Collectors.toUnmodifiableList());
    }

    public static void bakeConfig() {
        // fast regen
        fastRegenFoodThreshold = COMMON.fastRegenFoodThreshold.get();
        fastRegenIntervalTicks = COMMON.fastRegenIntervalTicks.get();
        fastRegenSaturationCap = COMMON.fastRegenSaturationCap.get().floatValue();

        // slow regen
        slowRegenFoodThreshold = COMMON.slowRegenFoodThreshold.get();
        slowRegenIntervalTicks = COMMON.slowRegenIntervalTicks.get();
        slowRegenHealAmount = COMMON.slowRegenHealAmount.get().floatValue();

        // exhaustion
        exhaustionStep = COMMON.exhaustionStep.get().floatValue();
        passiveExhaustionPerTick = COMMON.passiveExhaustionPerTick.get().floatValue();
        negativeExhaustionThreshold = COMMON.negativeExhaustionThreshold.get().floatValue();

        // exhaustion – jump
        exhaustionJumpSprinting = COMMON.jumpSprinting.get().floatValue();
        exhaustionJumpWalking = COMMON.jumpWalking.get().floatValue();

        // exhaustion – combat
        exhaustionAttackHit = COMMON.attackHit.get().floatValue();
        exhaustionDamageMultiplier = COMMON.damageMultiplier.get().floatValue();

        // exhaustion – movement
        exhaustionWalkOneBlock = COMMON.walkOneBlock.get().floatValue();
        exhaustionSprintOneBlock = COMMON.sprintOneBlock.get().floatValue();
        exhaustionSwimOneBlock = COMMON.swimOneBlock.get().floatValue();
        exhaustionWalkUnderwaterOneBlock = COMMON.walkUnderwaterOneBlock.get().floatValue();
        exhaustionWalkOnWaterOneBlock = COMMON.walkOnWaterOneBlock.get().floatValue();
        exhaustionClimbOneBlock = COMMON.climbOneBlock.get().floatValue();
        exhaustionCrouchOneBlock = COMMON.crouchOneBlock.get().floatValue();
        exhaustionAviateOneBlock = COMMON.aviateOneBlock.get().floatValue();
        exhaustionFlyOneBlock = COMMON.flyOneBlock.get().floatValue();

        // misc tweaks
        enableBottlePickupAdjustments = COMMON.enableBottlePickupAdjustments.get();
        enableFallingBlockBreakingAdjustments = COMMON.enableFallingBlockBreakingAdjustments.get();
        enableZombieVillagerCuring = COMMON.enableZombieVillagerCuring.get();
        zombifiedPiglinBurnRules = parseBurnRules(COMMON.zombifiedPiglinBurnRules.get());
        enableWaterPlantSourcePrevention = COMMON.enableWaterPlantSourcePrevention.get();

        // thermal patches
        enableThermalPatches = COMMON.enableThermalPatches.get();

        // obscure api patches
        hideObscureAPIMenuButton = COMMON.hideObscureAPIMenuButton.get();
    }

    public static class CommonConfig {
        // fast regen
        public final ForgeConfigSpec.IntValue fastRegenFoodThreshold;
        public final ForgeConfigSpec.IntValue fastRegenIntervalTicks;
        public final ForgeConfigSpec.DoubleValue fastRegenSaturationCap;

        // slow regen
        public final ForgeConfigSpec.IntValue slowRegenFoodThreshold;
        public final ForgeConfigSpec.IntValue slowRegenIntervalTicks;
        public final ForgeConfigSpec.DoubleValue slowRegenHealAmount;

        // exhaustion
        public final ForgeConfigSpec.DoubleValue exhaustionStep;
        public final ForgeConfigSpec.DoubleValue passiveExhaustionPerTick;
        public final ForgeConfigSpec.DoubleValue negativeExhaustionThreshold;

        // exhaustion – jump
        public final ForgeConfigSpec.DoubleValue jumpSprinting;
        public final ForgeConfigSpec.DoubleValue jumpWalking;

        // exhaustion – combat
        public final ForgeConfigSpec.DoubleValue attackHit;
        public final ForgeConfigSpec.DoubleValue damageMultiplier;

        // exhaustion – movement
        public final ForgeConfigSpec.DoubleValue walkOneBlock;
        public final ForgeConfigSpec.DoubleValue sprintOneBlock;
        public final ForgeConfigSpec.DoubleValue swimOneBlock;
        public final ForgeConfigSpec.DoubleValue walkUnderwaterOneBlock;
        public final ForgeConfigSpec.DoubleValue walkOnWaterOneBlock;
        public final ForgeConfigSpec.DoubleValue climbOneBlock;
        public final ForgeConfigSpec.DoubleValue crouchOneBlock;
        public final ForgeConfigSpec.DoubleValue aviateOneBlock;
        public final ForgeConfigSpec.DoubleValue flyOneBlock;

        // misc tweaks;
        public final ForgeConfigSpec.BooleanValue enableBottlePickupAdjustments;
        public final ForgeConfigSpec.BooleanValue enableFallingBlockBreakingAdjustments;
        public final ForgeConfigSpec.BooleanValue enableZombieVillagerCuring;
        public final ForgeConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>> zombifiedPiglinBurnRules;
        public final ForgeConfigSpec.BooleanValue enableWaterPlantSourcePrevention;
        public final ForgeConfigSpec.BooleanValue enableThermalPatches;
        public final ForgeConfigSpec.BooleanValue hideObscureAPIMenuButton;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("food");
            builder.push("regen");
            builder.push("fast");

            fastRegenFoodThreshold = builder
                    .comment("Minimum hunger level required for fast regeneration to occur.")
                    .defineInRange("food_threshold", 20, 0, 20);

            fastRegenIntervalTicks = builder
                    .comment("How often fast regeneration heals, in ticks.")
                    .defineInRange("interval_ticks", 10, 1, 20_000);

            fastRegenSaturationCap = builder
                    .comment("Maximum saturation value used when calculating fast regeneration healing.")
                    .defineInRange("saturation_cap", 6.0, 0.0, 20.0);

            builder.pop(); // fast

            builder.push("slow");

            slowRegenFoodThreshold = builder
                    .comment("Minimum hunger level required for slow regeneration to occur.")
                    .defineInRange("food_threshold", 18, 0, 20);

            slowRegenIntervalTicks = builder
                    .comment("How often slow regeneration heals, in ticks.")
                    .defineInRange("interval_ticks", 80, 1, 20_000);

            slowRegenHealAmount = builder
                    .comment("Amount of health restored per slow regeneration tick.")
                    .defineInRange("heal_amount", 1.0, 0.0, 20.0);

            builder.pop(); // slow
            builder.pop(); // regen

            builder.push("exhaustion");
            builder.push("jump");

            jumpWalking = builder
                    .comment("Exhaustion added when jumping while walking.")
                    .defineInRange("walking", 0.05, 0.0, 10.0);

            jumpSprinting = builder
                    .comment("Exhaustion added when jumping while sprinting.")
                    .defineInRange("sprinting", 0.2, 0.0, 10.0);

            builder.pop(); // jump

            builder.push("combat");

            attackHit = builder
                    .comment("Exhaustion added when attacking an entity.")
                    .defineInRange("attack", 0.1, 0.0, 40.0);

            damageMultiplier = builder
                    .comment("Exhaustion multiplier when taking damage.")
                    .defineInRange("damage_multiplier", 1.0, 0.0, 100.0);

            builder.pop(); // combat

            builder.push("movement");

            walkOneBlock = builder
                    .comment("Exhaustion added per block length walked.")
                    .defineInRange("walk_one_block", 0.0, 0.0, 40.0);

            sprintOneBlock = builder
                    .comment("Exhaustion added per block length sprinted.")
                    .defineInRange("sprint_one_block", 0.1, 0.0, 40.0);

            swimOneBlock = builder
                    .comment("Exhaustion added per block length swum.")
                    .defineInRange("swim_one_block", 0.01, 0.0, 40.0);

            walkUnderwaterOneBlock = builder
                    .comment("Exhaustion added per block length walked underwater.")
                    .defineInRange("walk_underwater_one_block", 0.01, 0.0, 40.0);

            walkOnWaterOneBlock = builder
                    .comment("Exhaustion added per block length walked on water.")
                    .defineInRange("walk_on_water_one_block", 0.01, 0.0, 40.0);

            climbOneBlock = builder
                    .comment("Exhaustion added per block length climbed.")
                    .defineInRange("climb_one_block", 0.0, 0.0, 40.0);

            crouchOneBlock = builder
                    .comment("Exhaustion added per block length crouched.")
                    .defineInRange("crouch_one_block", 0.0, 0.0, 40.0);

            aviateOneBlock = builder
                    .comment("Exhaustion added per block length flown with elytra.")
                    .defineInRange("aviate_one_block", 0.0, 0.0, 40.0);

            flyOneBlock = builder
                    .comment("Exhaustion added per block length moved in the air.")
                    .defineInRange("fly_one_block", 0.0, 0.0, 40.0);

            builder.pop(); // movement

            exhaustionStep = builder
                    .comment("How much exhaustion must accumulate before hunger or saturation is reduced.")
                    .comment("Functions as a global scale for hunger drain. Higher values mean slower hunger drain.")
                    .defineInRange("step", 4.0, 0.1, 40.0);

            passiveExhaustionPerTick = builder
                    .comment("Exhaustion added every tick regardless of player actions.")
                    .defineInRange("passive_per_tick", 0.0, 0.0, 40.0);

            negativeExhaustionThreshold = builder
                    .comment("Threshold for which exhaustion can be negative. If set to a value above -40, the exhaustion floor becomes that value.")
                    .comment("Useful if something has unintentionally caused negative exhaustion and you want it set to reasonable values")
                    .defineInRange("negative_exhaustion_threshold", -40.0, -40.0, 0.0);

            builder.pop(); // exhaustion
            builder.pop(); // food

            builder.push("misc");

            enableBottlePickupAdjustments = builder
                    .comment("Enable water bottle pickup adjustments, requiring 4 bottles to fill, and replacing the water source block")
                    .define("enable_bottle_pickup_adjustments", false);

            enableFallingBlockBreakingAdjustments = builder
                    .comment("Enable falling block adjustments, giving falling blocks a chance to break the partial blocks they land on")
                    .define("enable_falling_block_breaking_adjustments", true);

            enableZombieVillagerCuring = builder
                    .comment("Enable zombie villager conversion, disable to prevent zombie villagers from being cured")
                    .define("enable_zombie_villager_curing", true);

            zombifiedPiglinBurnRules = builder
                    .comment("Rules controlling whether zombified piglins lose fire immunity and burn in daylight.")
                    .comment("Each rule is an inline table with either 'dimension', 'biome', or 'structure' location options")
                    .comment("These three locations can be set to a string or list of strings, each representing their respective ID.")
                    .comment("Each table also contains a 'burn' boolean that controls which override is applied.")
                    .comment("Rules are evaluated in order, later rules taking priority.")
                    .comment("If no rule matches, vanilla fire immunity is kept.")
                    .comment("Applies to base zombified piglins, direct derivatives, and SpecialMobs variants.")
                    .comment("Example:")
                    .comment("  zombified_piglin_burn_rules = [")
                    .comment("      { dimension = \"minecraft:overworld\", burn = true },")
                    .comment("      { biome = \"minecraft:ice_spikes\", burn = false },")
                    .comment("      { structure = \"example:piglin_defense_dome\", burn = false },")
                    .comment("  ]")
                    .comment("Makes the piglins burn in the overworld, unless they are in the ice spikes biome or within a piglin defense dome structure.")
                    .defineListAllowEmpty(
                            List.of("zombified_piglin_burn_rules"),
                            List::of,
                            BurnRule::isValidConfigEntry
                    );

            enableWaterPlantSourcePrevention = builder
                    .comment("Prevent flowing water from becoming a source via block placement.")
                    .comment("- Water plants (kelp, seagrass, Farmer's Delight rice) cannot be placed or grown in flowing water")
                    .comment("- Waterloggable blocks (ladders, fences, signs, stairs, slabs, etc.) placed in flowing water are not waterlogged")
                    .define("enable_water_plant_source_prevention", false);

            builder.pop(); // misc

            builder.push("compat");

            enableThermalPatches = builder
                    .comment("Enable Thermal series compat patches")
                    .comment("- Fix Thermal machines not recognizing mod-modified stack sizes")
                    .comment("- Disable crafting remainders in machines")
                    .comment("- Make Chiller, Refinery, and single-fluid Dynamos ignore fluid NBT for recipe/fuel matching")
                    .define("enable_thermal_patches", false);

            hideObscureAPIMenuButton = builder
                    .comment("Hide the Obscure API title-screen button.")
                    .comment("The button is added by Obscure API and cannot normally be hidden, only repositioned.")
                    .define("hide_obscure_api_menu_button", false);

            builder.pop(); // compat
        }
    }
}