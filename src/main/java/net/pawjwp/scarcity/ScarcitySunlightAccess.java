package net.pawjwp.scarcity;

// Exposes a Mob's sunlight sensitivity state
public interface ScarcitySunlightAccess {
    SunlightSensitivity.State scarcity$getSunlightState();
    void scarcity$setSunlightState(SunlightSensitivity.State state);
}
