package net.pawjwp.scarcity;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.pawjwp.scarcity.config.ScarcityConfig;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// A configured rule that determines if a zombified piglin burn in the daylight.
// Each rule matches on a dimension, biome, or structure, with later rules overriding earlier ones.
public record BurnRule(Type type, Set<ResourceLocation> targets, boolean burn) {

    // Define the three types of locations a rule can match to.
    public enum Type {
        DIMENSION, BIOME, STRUCTURE;
        final String key = name().toLowerCase();
    }
    public enum Outcome { BURN, NO_BURN, NO_RULE }

    // Outcome of the burn evaluation, stored on the entity and synced from server to client.
    // Need to sync because structure detection only works server-side.
    public static final EntityDataAccessor<Byte> OUTCOME_DATA = createOutcomeAccessor();

    private static EntityDataAccessor<Byte> createOutcomeAccessor() {
        try {
            Class.forName(ZombifiedPiglin.class.getName(), true, ZombifiedPiglin.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return SynchedEntityData.defineId(ZombifiedPiglin.class, EntityDataSerializers.BYTE);
    }

    // Reads the outcome stored on the entity.
    public static Outcome currentOutcome(Entity entity) {
        return Outcome.values()[entity.getEntityData().get(OUTCOME_DATA)];
    }

    // Determines the outcome by checking each rule starting at the bottom.
    // If nothing matches, use NO_RULE, which defaults to vanilla.
    public static Outcome evaluate(Entity entity) {
        List<BurnRule> rules = ScarcityConfig.zombifiedPiglinBurnRules;
        for (int i = rules.size() - 1; i >= 0; i--) {
            BurnRule rule = rules.get(i);
            if (rule.matches(entity)) {
                return rule.burn() ? Outcome.BURN : Outcome.NO_BURN;
            }
        }
        return Outcome.NO_RULE;
    }

    // Checks whether this rule's condition currently applies to the piglin's location.
    private boolean matches(Entity entity) {
        return switch (type) {
            case DIMENSION -> targets.contains(entity.level().dimension().location());
            case BIOME -> entity.level().getBiome(entity.blockPosition()).unwrapKey()
                    .map(key -> targets.contains(key.location())).orElse(false);
            case STRUCTURE -> entity.level() instanceof ServerLevel server && matchesStructure(server, entity);
        };
    }

    // Checks whether the piglin is in the rule's structure
    private boolean matchesStructure(ServerLevel level, Entity entity) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (Structure structure : level.structureManager().getAllStructuresAt(entity.blockPosition()).keySet()) {
            if (targets.contains(registry.getKey(structure))) {
                return true;
            }
        }
        return false;
    }

    // Gets a burn rule from a config entry
    public static BurnRule fromConfig(UnmodifiableConfig cfg) {
        boolean burn = cfg.get("burn");
        for (Type t : Type.values()) {
            Object raw = cfg.get(t.key);
            if (raw != null) {
                return new BurnRule(t, toIds(raw), burn);
            }
        }
        throw new IllegalStateException("Burn rule has no condition");
    }

    // Determines IDs from configured values
    private static Set<ResourceLocation> toIds(Object raw) {
        List<?> values = raw instanceof List<?> list ? list : List.of(raw);
        return values.stream()
                .map(s -> ResourceLocation.parse((String) s))
                .collect(Collectors.toUnmodifiableSet());
    }

    // Checks if config entry is valid before it is used.
    // Valid if it has a burn boolean and either dimension, biome, or structure, as a direct entry or a list.
    public static boolean isValidConfigEntry(Object obj) {
        if (!(obj instanceof UnmodifiableConfig cfg)) return false;
        if (!(cfg.get("burn") instanceof Boolean)) return false;
        int conditionCount = 0;
        for (Type t : Type.values()) {
            Object value = cfg.get(t.key);
            if (value == null) continue;
            conditionCount++;
            if (!isValidIdOrIdList(value)) return false;
        }
        return conditionCount == 1;
    }

    // Checks if location entry is a vlid resouce location string or list of strings.
    private static boolean isValidIdOrIdList(Object value) {
        if (value instanceof String s) {
            return ResourceLocation.tryParse(s) != null;
        }
        if (value instanceof List<?> list && !list.isEmpty()) {
            return list.stream().allMatch(o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        }
        return false;
    }
}
