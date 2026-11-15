![Scarcity](https://raw.githubusercontent.com/pawjwp/Scarcity/refs/heads/main/scarcity-banner-medium.png)

## About

Scarcity adds various compatibility and quality of life changes to mods like Ex Deorum, Thirst Was Reclaimed, and Cold Sweat, plus options to make resources a bit more scarce. These include configurable hunger and regeneration values, water source limitations, nerfed falling block physics, seeds for plants that normally don't have them, gear for managing thirst and temperature.

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

### Thirst Management Gear (For Thirst Was Reclaimed)
- Camel Packs
  - Wearable tanks that automatically drink for you
  - The Basic Camel Pack can carry 4 buckets of liquid and purifies its water by 1 level
  - The Advanced Camel pack can carry 20 buckets of liquid and purifies its water by 2 levels
  - The Condensentrating Camel Pack passively collects water from the air and never needs filling
- Water Filter
  - Craft with a water container to increase the purity of that container by one level

### Temperature Management Gear (For Cold Sweat)
- Desert and Snow Armor
  - Leather armor equivalents that protect against heat or cold
- Leather and Wool Insulation
  - Simple insulation items that provide temperature protection from the insulation Curios slot
- Stillsuit
  - Insulates against both heat and cold, and slows down how quickly you get thirsty
  - Also worn in the insulation slot

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

### Thirst Water Purity
- Configurable purity for water that has no purity tag, since Thirst Was Reclaimed has changed its default between versions
- Option to add a purity tag to all untagged water as it enters a player's inventory (applies to each item in the `scarcity:force_purity` tag)
- Fix various mods losing water purity
  - AE2 storage buses only extract water of the requested purity
  - Create pipes extracting from cauldrons and dispensers on contraption maintain purity
  - Ex Deorum porcelain buckets maintain purity
  - Forge default world water pickups maintain purity (used by the Thermal Reservoirs and the Sophisticated Series pump upgrade)
  - Industrial Foregoing fluid collectors maintain purity
  - Mekanism electric pumps and tanks maintain purity
  - PneumaticCraft gas lifts, liquid hoppers, and drones maintain purity
  - Refurbished Furniture blocks with internal tanks maintain purity and use 250 mb per bottle instead of 1000
  - Thermal Series machines and dynamos accept all purity values, and maintain purity when used with water bottles
  - Tinkers' Construct tanks, casting, and tools maintain purity
- Merge water of different purities in tanks that Thirst Was Reclaimed doesn't cover
  - No effect with Thirst Was Taken, which never merges water of different purities
  - Ender IO: Tanks and machines, including the Fluid Tank item
  - Ex Deorum: Barrels and crucibles
  - Mekanism: Tanks, including the Portable Fluid Tank's bucket mode
  - PneumaticCraft: Tanks and machines
  - Sophisticated Backpacks/Storage: Tank upgrades
  - Thermal: Tanks, machines, Fluid Cells, and items that hold fluid like the Fluid Reservoir
  - Tinkers' Construct: Tanks, the smeltery/foundry, and tools that pick up fluids
  - Other items that hold fluid, using Forge's standard item tank
- Change how purity is calculated when merging water of different purities in tanks
  - Use the lower purity, matching Thirst Was Reclaimed
  - Take a weighted average, then round to the nearest purity
  - Take a weighted average, then round down
  - Take a weighted average, then round up
  - Take a weighted average, then round up or down at random based on the fractional value
  - Round up only when the fraction is above a configurable threshold

### New Seeds
- New seeds for plants that do not normally have them, intended for modpack makers on skyblock-style maps (seeds do not spawn or drop naturally and must be added to loot tables to be obtainable)
- Seeds for all vanilla saplings (oak, spruce, birch, jungle, acacia, dark oak, cherry, but not including non-sapling trees like mangrove, crimson, or warped)
- Seeds for foods that don't already have them (potatoes, carrots, and sweet berries)
- Seeds for other plants (cactus, sugar cane, and bamboo)
- Seeds for some other modded plants (currently the Farmer's Delight Onion and Thermal's rubberwood sapling)

### Misc
- Falling block breaking adjustments, giving them a chance to break partial blocks they land on (chance is based on the broken block's hardness)
- Thermal Series compat patches
  - Fix Thermal machines not recognizing stack sizes changed by other mods
  - Disable crafting remainders in machines (for example, smelting a bucket of fluid into another bucket of fluid will no longer leave the bucket behind)
  - Allow the Fluid Encapsulator to fill Florbs with fluids that have NBT
- Ender IO compat patches
  - Fluid conduits maintain fluid NBT, including conduits locked to one fluid
  - Fluid Tanks keep fluid NBT when filling or emptying fluid containers
  - The Fluid Tank fills or empties stacks of fluid containers one item at a time
- Option to disable zombie villager curing
- Sunlight sensitivity NBT tag system that can make any mob lose fire invulnerability and burn in the daylight
- Option to hide the Obscure API title screen button

## Suggestions

You can reach me with suggestions or bug reports by making an issue on the [GitHub](https://github.com/pawjwp/Scarcity/issues) page. If preferred, I will respond to any messages in the [Discord](https://discord.gg/4en3SpWtJg) server for my current modpack, [Desolate Planet](https://modrinth.com/modpack/desolate-planet/). Feel free to reach out to me there as well.

_Any proceeds from this mod will be donated to [GiveWell](https://www.givewell.org/)._