package net.pawjwp.scarcity.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

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
    public static boolean enableWaterPlantSourcePrevention;

    // thermal patches
    public static boolean enableThermalPatches;

    // ender io patches
    public static boolean enableEnderIOPatches;

    // thirst patches
    public static boolean enableThirstPurityMerging;
    public static PurityMergeMethod thirstPurityMergeMethod = PurityMergeMethod.LOWEST; // Load lowest by default so mixins can't read null values while the config loads
    public static double thirstPurityRoundingThreshold;
    public static int thirstUntaggedWaterPurity = -1; // Keep Thirst's own default until the config loads, since 0 would mean dirty
    public static boolean enableThirstPurityFixes;

    // obscure api patches
    public static boolean hideObscureAPIMenuButton;

    // ex deorum compat features
    public static boolean enableTinkersTools;
    public static boolean enableExDeorumToolOverrides;
    public static boolean enableMachineSieve;
    public static int machineSieveEnergyPerTick;
    public static int machineSieveEnergyPerOperation;
    public static double heavyAgitatorPowerMultiplier;
    public static double heavyAgitatorEnergyMultiplier;

    // camel packs
    public static int camelPackSipAmount;
    public static double camelPackBottleThirst;
    public static double camelPackBottleQuenched;
    // load default capacities at the start to avoid size 0 issues
    public static int camelPackCapacity = 4000;
    public static int advancedCamelPackCapacity = 20000;
    public static double camelPackThirstMultiplier;
    public static double camelPackQuenchMultiplier;
    public static int camelPackPurityBonus;
    public static double advancedCamelPackThirstMultiplier;
    public static double advancedCamelPackQuenchMultiplier;
    public static int advancedCamelPackPurityBonus;
    public static double unlimitedCamelPackThirstMultiplier;
    public static double unlimitedCamelPackQuenchMultiplier;

    // stillsuit
    public static double stillsuitThirstMultiplier = 0.5; // load the default to avoid size 0 issues
    public static double stillsuitColdInsulation;
    public static double stillsuitHeatInsulation;

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
        enableWaterPlantSourcePrevention = COMMON.enableWaterPlantSourcePrevention.get();

        // thermal patches
        enableThermalPatches = COMMON.enableThermalPatches.get();

        // ender io patches
        enableEnderIOPatches = COMMON.enableEnderIOPatches.get();

        // thirst patches
        enableThirstPurityMerging = COMMON.enableThirstPurityMerging.get();
        thirstPurityMergeMethod = COMMON.thirstPurityMergeMethod.get();
        thirstPurityRoundingThreshold = COMMON.thirstPurityRoundingThreshold.get();
        thirstUntaggedWaterPurity = COMMON.thirstUntaggedWaterPurity.get();
        enableThirstPurityFixes = COMMON.enableThirstPurityFixes.get();

        // obscure api patches
        hideObscureAPIMenuButton = COMMON.hideObscureAPIMenuButton.get();

        // ex deorum compat features
        enableTinkersTools = COMMON.enableTinkersTools.get();
        enableExDeorumToolOverrides = COMMON.enableExDeorumToolOverrides.get();
        enableMachineSieve = COMMON.enableMachineSieve.get();
        machineSieveEnergyPerTick = COMMON.machineSieveEnergyPerTick.get();
        machineSieveEnergyPerOperation = COMMON.machineSieveEnergyPerOperation.get();
        heavyAgitatorPowerMultiplier = COMMON.heavyAgitatorPowerMultiplier.get();
        heavyAgitatorEnergyMultiplier = COMMON.heavyAgitatorEnergyMultiplier.get();

        // camel packs
        camelPackSipAmount = COMMON.camelPackSipAmount.get();
        camelPackBottleThirst = COMMON.camelPackBottleThirst.get();
        camelPackBottleQuenched = COMMON.camelPackBottleQuenched.get();
        camelPackCapacity = COMMON.camelPackCapacity.get();
        advancedCamelPackCapacity = COMMON.advancedCamelPackCapacity.get();
        camelPackThirstMultiplier = COMMON.camelPackThirstMultiplier.get();
        camelPackQuenchMultiplier = COMMON.camelPackQuenchMultiplier.get();
        camelPackPurityBonus = COMMON.camelPackPurityBonus.get();
        advancedCamelPackThirstMultiplier = COMMON.advancedCamelPackThirstMultiplier.get();
        advancedCamelPackQuenchMultiplier = COMMON.advancedCamelPackQuenchMultiplier.get();
        advancedCamelPackPurityBonus = COMMON.advancedCamelPackPurityBonus.get();
        unlimitedCamelPackThirstMultiplier = COMMON.unlimitedCamelPackThirstMultiplier.get();
        unlimitedCamelPackQuenchMultiplier = COMMON.unlimitedCamelPackQuenchMultiplier.get();

        // stillsuit
        stillsuitThirstMultiplier = COMMON.stillsuitThirstMultiplier.get();
        stillsuitColdInsulation = COMMON.stillsuitColdInsulation.get();
        stillsuitHeatInsulation = COMMON.stillsuitHeatInsulation.get();
    }

    // How the purity is chosen when water of different purities merges in a tank
    public enum PurityMergeMethod { LOWEST, NEAREST, FLOOR, CEILING, RANDOM, THRESHOLD }

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
        public final ForgeConfigSpec.BooleanValue enableWaterPlantSourcePrevention;
        public final ForgeConfigSpec.BooleanValue enableThermalPatches;
        public final ForgeConfigSpec.BooleanValue enableEnderIOPatches;
        public final ForgeConfigSpec.BooleanValue enableThirstPurityMerging;
        public final ForgeConfigSpec.EnumValue<PurityMergeMethod> thirstPurityMergeMethod;
        public final ForgeConfigSpec.DoubleValue thirstPurityRoundingThreshold;
        public final ForgeConfigSpec.IntValue thirstUntaggedWaterPurity;
        public final ForgeConfigSpec.BooleanValue enableThirstPurityFixes;
        public final ForgeConfigSpec.BooleanValue hideObscureAPIMenuButton;

        // ex deorum compat features
        public final ForgeConfigSpec.BooleanValue enableTinkersTools;
        public final ForgeConfigSpec.BooleanValue enableExDeorumToolOverrides;
        public final ForgeConfigSpec.BooleanValue enableMachineSieve;
        public final ForgeConfigSpec.IntValue machineSieveEnergyPerTick;
        public final ForgeConfigSpec.IntValue machineSieveEnergyPerOperation;
        public final ForgeConfigSpec.DoubleValue heavyAgitatorPowerMultiplier;
        public final ForgeConfigSpec.DoubleValue heavyAgitatorEnergyMultiplier;

        // camel packs
        public final ForgeConfigSpec.IntValue camelPackSipAmount;
        public final ForgeConfigSpec.DoubleValue camelPackBottleThirst;
        public final ForgeConfigSpec.DoubleValue camelPackBottleQuenched;
        public final ForgeConfigSpec.IntValue camelPackCapacity;
        public final ForgeConfigSpec.IntValue advancedCamelPackCapacity;
        public final ForgeConfigSpec.DoubleValue camelPackThirstMultiplier;
        public final ForgeConfigSpec.DoubleValue camelPackQuenchMultiplier;
        public final ForgeConfigSpec.IntValue camelPackPurityBonus;
        public final ForgeConfigSpec.DoubleValue advancedCamelPackThirstMultiplier;
        public final ForgeConfigSpec.DoubleValue advancedCamelPackQuenchMultiplier;
        public final ForgeConfigSpec.IntValue advancedCamelPackPurityBonus;
        public final ForgeConfigSpec.DoubleValue unlimitedCamelPackThirstMultiplier;
        public final ForgeConfigSpec.DoubleValue unlimitedCamelPackQuenchMultiplier;

        // stillsuit
        public final ForgeConfigSpec.DoubleValue stillsuitThirstMultiplier;
        public final ForgeConfigSpec.DoubleValue stillsuitColdInsulation;
        public final ForgeConfigSpec.DoubleValue stillsuitHeatInsulation;

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
                    .comment("Also applies to dispensers")
                    .define("enable_bottle_pickup_adjustments", false);

            enableFallingBlockBreakingAdjustments = builder
                    .comment("Enable falling block adjustments, giving falling blocks a chance to break the partial blocks they land on")
                    .define("enable_falling_block_breaking_adjustments", true);

            enableZombieVillagerCuring = builder
                    .comment("Enable zombie villager conversion, disable to prevent zombie villagers from being cured")
                    .define("enable_zombie_villager_curing", true);

            enableWaterPlantSourcePrevention = builder
                    .comment("Prevent flowing water from becoming a source via block placement.")
                    .comment("- Water plants (kelp, seagrass, Farmer's Delight rice) cannot be placed or grown in flowing water")
                    .comment("- Waterloggable blocks (ladders, fences, signs, stairs, slabs, etc.) placed in flowing water are not waterlogged")
                    .define("enable_water_plant_source_prevention", false);

            builder.pop(); // misc

            builder.push("compat");

            enableThermalPatches = builder
                    .comment("Enable Thermal Series compat patches")
                    .comment("- Fix Thermal machines not recognizing stack sizes changed by other mods")
                    .comment("- Disable crafting remainders in machines")
                    .comment("- Allow the Fluid Encapsulator to fill Florbs with fluids that have NBT")
                    .define("enable_thermal_patches", false);

            enableEnderIOPatches = builder
                    .comment("Enable Ender IO compat patches")
                    .comment("- Fluid conduits maintain fluid NBT, including conduits locked to one fluid")
                    .comment("- Fluid Tanks keep fluid NBT when filling or emptying fluid containers")
                    .comment("- The Fluid Tank fills or empties stacks of fluid containers one item at a time")
                    .define("enable_enderio_patches", true);

            thirstUntaggedWaterPurity = builder
                    .comment("Purity of water with no purity tag, -1 to use Thirst's default")
                    .defineInRange("thirst_untagged_water_purity", -1, -1, 3);

            enableThirstPurityFixes = builder
                    .comment("Fix various mods losing water purity")
                    .comment("- AE2 storage buses only extract water of the requested purity")
                    .comment("- Create pipes extracting from cauldrons and dispensers on contraption maintain purity")
                    .comment("- Ex Deorum porcelain buckets maintain purity")
                    .comment("- Forge default world water pickups maintain purity (used by the Thermal Reservoirs and the Sophisticated Series pump upgrade)")
                    .comment("- Industrial Foregoing fluid collectors maintain purity")
                    .comment("- Mekanism electric pumps and tanks maintain purity")
                    .comment("- PneumaticCraft gas lifts, liquid hoppers, and drones maintain purity")
                    .comment("- Refurbished Furniture blocks with internal tanks maintain purity and use 250 mb per bottle instead of 1000")
                    .comment("- Thermal Series machines and dynamos accept all purity values, and maintain purity when used with water bottles")
                    .comment("- Tinkers' Construct tanks, casting, and tools maintain purity")
                    .define("enable_thirst_purity_fixes", false);

            enableThirstPurityMerging = builder
                    .comment("Merge water of different purities in tanks that Thirst Was Reclaimed doesn't cover, using thirst_purity_merge_method")
                    .comment("No effect with Thirst Was Taken, which never merges water of different purities")
                    .comment("- Ender IO: Tanks and machines, including the Fluid Tank item")
                    .comment("- Ex Deorum: Barrels and crucibles")
                    .comment("- Mekanism: Tanks, including the Portable Fluid Tank's bucket mode")
                    .comment("- PneumaticCraft: Tanks and machines")
                    .comment("- Sophisticated Backpacks/Storage: Tank upgrades")
                    .comment("- Thermal: Tanks, machines, Fluid Cells, and items that hold fluid like the Fluid Reservoir")
                    .comment("- Tinkers' Construct: Tanks, the smeltery/foundry, and tools that pick up fluids")
                    .comment("- Other items that hold fluid, using Forge's standard item tank")
                    .define("enable_thirst_purity_merging", false);

            thirstPurityMergeMethod = builder
                    .comment("Change how purity is calculated when merging water of different purities in tanks")
                    .comment("No effect if using Thirst Was Taken instead of Thirst Was Reclaimed")
                    .comment("- LOWEST: Use the lower purity, matching Thirst Was Reclaimed")
                    .comment("- NEAREST: Take a weighted average, then round to the nearest purity")
                    .comment("- FLOOR: Take a weighted average, then round down")
                    .comment("- CEILING: Take a weighted average, then round up")
                    .comment("- RANDOM: Take a weighted average, then round up or down at random based on the fractional value")
                    .comment("- THRESHOLD: Round up only when the fraction is above thirst_purity_rounding_threshold")
                    .comment("Warning: NEAREST, CEILING, and THRESHOLD can be exploited to purify water by merging it in small amounts")
                    .defineEnum("thirst_purity_merge_method", PurityMergeMethod.LOWEST);

            thirstPurityRoundingThreshold = builder
                    .comment("Threshold at which the THRESHOLD merge method will round the average purity up instead of down")
                    .comment("For example, 0.9 rounds an average purity of 2.91 up to 3 and 2.89 down to 2")
                    .comment("No effect if thirst_purity_merge_method is not set to THRESHOLD")
                    .defineInRange("thirst_purity_rounding_threshold", 0.9, 0.0, 1.0);

            hideObscureAPIMenuButton = builder
                    .comment("Hide the Obscure API title-screen button.")
                    .comment("The button is added by Obscure API and cannot normally be hidden, only repositioned.")
                    .define("hide_obscure_api_menu_button", false);

            enableTinkersTools = builder
                    .comment("Enable the Tinkers Construct Crook and Crushing Hammer.")
                    .comment("Requires Tinkers Construct and Ex Deorum.")
                    .define("enable_tinkers_tools", true);

            enableExDeorumToolOverrides = builder
                    .comment("Enable Ex Deorum overrides for Scarcity's Tinkers tools:")
                    .comment("- Broken tools no longer work as hammers/crooks")
                    .comment("- The Heavy-Hammering modifier makes the Crushing Hammer act as a compressed hammer")
                    .comment("- Tinkers tools inside the Mechanical Hammer break instead of being destroyed")
                    .define("enable_exdeorum_tool_overrides", true);

            enableMachineSieve = builder
                    .comment("Enable the Particulate Sieve machine and Heavy Agitator augment.")
                    .comment("Requires Thermal and Ex Deorum.")
                    .define("enable_machine_sieve", true);

            machineSieveEnergyPerTick = builder
                    .comment("Base energy per tick used by the Particulate Sieve.")
                    .defineInRange("machine_sieve_energy_per_tick", 40, 1, 100_000);

            machineSieveEnergyPerOperation = builder
                    .comment("Base energy consumed by one sieve operation.")
                    .defineInRange("machine_sieve_energy_per_operation", 4000, 1, 10_000_000);

            heavyAgitatorPowerMultiplier = builder
                    .comment("Energy consumption multiplier from the Heavy Agitator augment.")
                    .defineInRange("heavy_agitator_power_multiplier", 2.0, 0.1, 100.0);

            heavyAgitatorEnergyMultiplier = builder
                    .comment("Total energy per operation multiplier from the Heavy Agitator augment.")
                    .comment("Determines process speed combined with the power multiplier")
                    .defineInRange("heavy_agitator_energy_multiplier", 6.0, 0.1, 100.0);

            builder.push("camel_pack");

            camelPackSipAmount = builder
                    .comment("Fluid drained from a camel pack each time it is drank from (mB).")
                    .defineInRange("sip_amount", 100, 1, 1000);

            camelPackBottleThirst = builder
                    .comment("Thirst from drinking one bottle (250 mB) of water from a camel pack (before applying its thirst multiplier).")
                    .defineInRange("bottle_thirst", 5.0, 0.0, 20.0);

            camelPackBottleQuenched = builder
                    .comment("Quenched from drinking one bottle (250 mB) of water from a camel pack (before applying its quench multiplier).")
                    .defineInRange("bottle_quenched", 8.0, 0.0, 20.0);

            camelPackCapacity = builder
                    .comment("Tank capacity of the Camel Pack (mB).")
                    .defineInRange("capacity", 4000, 1, 1_000_000);

            camelPackThirstMultiplier = builder
                    .comment("Multiplier for thirst restored by the Camel Pack.")
                    .defineInRange("thirst_multiplier", 1.0, 0.0, 100.0);

            camelPackQuenchMultiplier = builder
                    .comment("Multiplier for quenched restored by the Camel Pack.")
                    .defineInRange("quench_multiplier", 1.0, 0.0, 100.0);

            camelPackPurityBonus = builder
                    .comment("Purity level added to the water drank from the Camel Pack.")
                    .defineInRange("purity_bonus", 1, 0, 3);

            advancedCamelPackCapacity = builder
                    .comment("Tank capacity of the Advanced Camel Pack (mB).")
                    .defineInRange("advanced_capacity", 20000, 1, 1_000_000);

            advancedCamelPackThirstMultiplier = builder
                    .comment("Multiplier for thirst restored by the Advanced Camel Pack.")
                    .defineInRange("advanced_thirst_multiplier", 1.0, 0.0, 100.0);

            advancedCamelPackQuenchMultiplier = builder
                    .comment("Multiplier for quenched restored by the Advanced Camel Pack.")
                    .defineInRange("advanced_quench_multiplier", 1.25, 0.0, 100.0);

            advancedCamelPackPurityBonus = builder
                    .comment("Purity level added to the water drank from the Advanced Camel Pack")
                    .defineInRange("advanced_purity_bonus", 2, 0, 3);

            unlimitedCamelPackThirstMultiplier = builder
                    .comment("Multiplier for the thirst restored by the Condensentrating Camel Pack.")
                    .defineInRange("unlimited_thirst_multiplier", 1.5, 0.0, 100.0);

            unlimitedCamelPackQuenchMultiplier = builder
                    .comment("Multiplier for the quenched restored by the Condensentrating Camel Pack.")
                    .defineInRange("unlimited_quench_multiplier", 1.5, 0.0, 100.0);

            builder.pop(); // camel_pack

            builder.push("stillsuit");

            stillsuitThirstMultiplier = builder
                    .comment("Multiplier for how much thirst is gained when wearing a stillsuit.")
                    .comment("The default of 0.5 means 50% slower thirst drain rate.")
                    .defineInRange("thirst_multiplier", 0.5, 0.0, 10.0);

            stillsuitColdInsulation = builder
                    .comment("Cold insulation from wearing a stillsuit.")
                    .defineInRange("cold_insulation", 50.0, 0.0, 1000.0);

            stillsuitHeatInsulation = builder
                    .comment("Heat insulation from wearing a stillsuit.")
                    .defineInRange("heat_insulation", 50.0, 0.0, 1000.0);

            builder.pop(); // stillsuit

            builder.pop(); // compat
        }
    }
}