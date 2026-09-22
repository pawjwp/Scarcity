![Scarcity](https://raw.githubusercontent.com/pawjwp/Scarcity/refs/heads/main/scarcity-banner-medium.png)

## About

Scarcity adds options to make resources a bit more scarce, including configurable hunger and regeneration values, water source limitations, nerfed falling block physics, sunlight sensitivity, seeds for plants that normally don't have them, and compatibility features for several other mods.

This mod was created for my modpack, [Desolate Planet](https://modrinth.com/modpack/desolate-planet/), but the features it adds will hopefully be useful to other modpacks as well.

## Features

*Nearly every feature can be enabled or disabled in the config.*

### Ex Deorum Addons
- Particulate Sieve
  - A new sieve based on Thermal Series machines, only present with Thermal installed
  - Uses the same output tables that Ex Deorum sieves do
  - Supports Thermal machine configuration and augments
  - A new "Heavy Agitator" augment allows sifting compressed blocks
- Tinkers' Crushing Hammer
  - New modular hammers, only present with Tinkers' Construct installed
  - The "Heavy Hammering" modifier allows the hammer to break compressed blocks
- Tinkers' Crook
  - New modular crooks, only present with Tinkers' Construct installed

### Configurable Hunger/Saturation Rates
- Configurable hunger depletion rate from:
  - Movement, including walking, sprinting, jumping, swimming, and more
  - Attacking and taking damage
  - Global exhaustion gain rate
  - Optional passive exhaustion drain
- Configurable hunger-based regeneration rate, including:
  - Fast and slow regeneration rates
  - Hunger level thresholds at which fast and slow regeneration occur
  - Hunger depletion rate during regeneration
  - Custom attributes for overall, fast, and slow regeneration speed

### Water Source Limitations
- Limited water bottle pickup, consuming the water source block and filling four bottles at a time
- Water source block prevention to stop kelp, seagrass, rice (from Farmer's Delight), and generic waterloggable blocks from converting flowing water into source blocks

### New Seeds
- New seeds for plants that do not normally have them, intended for modpack makers on skyblock-style maps (seeds do not spawn or drop naturally and must be added to loot tables to be obtainable)
- Seeds for all vanilla saplings (oak, spruce, birch, jungle, acacia, dark oak, cherry, but not including non-sapling trees like mangrove, crimson, or warped)
- Seeds for foods that don't already have them (potatoes, carrots, and sweet berries)
- Seeds for other plants (cactus, sugar cane, and bamboo)
- Seeds for some other modded plants (currently the Farmer's Delight Onion and Thermal's rubberwood sapling)

### Misc
- Falling block breaking adjustments, giving them a chance to break partial blocks they land on (chance is based on the broken block's hardness)
- Thermal Series bug fixes
  - Fixed Thermal machines failing to process fluid recipes if the fluids have NBT
  - Fixed Thermal machines failing to recognize when another mod has updated an item's stack size
  - Fixed Thermal machines leaving crafting remainders when they should be consumed (for example, smelting a bucket of fluid into another bucket of fluid will no longer leave the bucket behind)
- Option to disable zombie villager curing
- Sunlight sensitivity NBT tag system that can make any mob lose fire invulnerability and burn in the daylight
- Option to hide the Obscure API title screen button

## Suggestions

You can reach me with suggestions or bug reports by making an issue on the [GitHub](https://github.com/pawjwp/Scarcity/issues) page. If preferred, I will respond to any messages in the [Discord](https://discord.gg/4en3SpWtJg) server for my current modpack, [Desolate Planet](https://modrinth.com/modpack/desolate-planet/). Feel free to reach out to me there as well.

_Any proceeds from this mod will be donated to [GiveWell](https://www.givewell.org/)._