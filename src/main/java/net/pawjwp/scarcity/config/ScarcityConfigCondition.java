package net.pawjwp.scarcity.config;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.pawjwp.scarcity.Scarcity;

// Recipe condition tied to the config, so disabling a feature can remove its recipes
public record ScarcityConfigCondition(String flag) implements ICondition {
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "config");

    public static final String TINKERS_TOOLS = "enable_tinkers_tools";

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return switch (this.flag) {
            case TINKERS_TOOLS -> ScarcityConfig.enableTinkersTools;
            default -> throw new IllegalArgumentException("Unknown config flag: " + this.flag);
        };
    }

    public static class Serializer implements IConditionSerializer<ScarcityConfigCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ScarcityConfigCondition condition) {
            json.addProperty("flag", condition.flag);
        }

        @Override
        public ScarcityConfigCondition read(JsonObject json) {
            return new ScarcityConfigCondition(json.get("flag").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
