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
    }

    public static class CommonConfig {
        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("food");
            builder.pop(); // food
        }
    }
}