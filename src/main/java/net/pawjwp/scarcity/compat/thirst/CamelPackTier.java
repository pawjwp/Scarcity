package net.pawjwp.scarcity.compat.thirst;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

// Settings associated with a camel pack tier
// If hasTank is true, it has a fluid tank and consumes liquid, if false it provides infinite water
// purityBonus is the purity level added to drunk water (the "built-in filter" of the camel pack)
public record CamelPackTier(String id, boolean hasTank, IntSupplier capacity, DoubleSupplier thirstMult, DoubleSupplier quenchMult, IntSupplier purityBonus) {
}
