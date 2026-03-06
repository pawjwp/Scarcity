package net.pawjwp.scarcity.attribute;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;

public class ScarcityAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Scarcity.MOD_ID);

    public static final RegistryObject<Attribute> REGEN_SPEED = ATTRIBUTES.register(
            "regen_speed",
            () -> new RangedAttribute("attribute.scarcity.regen_speed", 1.0, 0.0, 1024.0)
                    .setSyncable(true)
    );

    public static final RegistryObject<Attribute> FAST_REGEN_SPEED = ATTRIBUTES.register(
            "fast_regen_speed",
            () -> new RangedAttribute("attribute.scarcity.fast_regen_speed", 1.0, 0.0, 1024.0)
                    .setSyncable(true)
    );

    public static final RegistryObject<Attribute> SLOW_REGEN_SPEED = ATTRIBUTES.register(
            "slow_regen_speed",
            () -> new RangedAttribute("attribute.scarcity.slow_regen_speed", 1.0, 0.0, 1024.0)
                    .setSyncable(true)
    );

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, REGEN_SPEED.get());
        event.add(EntityType.PLAYER, FAST_REGEN_SPEED.get());
        event.add(EntityType.PLAYER, SLOW_REGEN_SPEED.get());
    }
}
