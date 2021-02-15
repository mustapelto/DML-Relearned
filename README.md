## This mod is currently in alpha!
Some major features from the original mod have not yet been implemented (most importantly Trials, JEI integration and the Guidebook),
and *there will be bugs!* Do not use this on an important world that you don't have a backup of. **You have been warned!**

# Deep Mob Learning: Relearned
A rewrite from scratch of [DeepMobLearning](https://github.com/xt9/DeepMobLearning) by xt9/IterationFunk.
Original textures by xt9.

A server-friendly and peaceful-compatible mod for mob loot acquisition, originally inspired by the Soul Shards mod,
where you could "collect" mob kills to later use them for mob spawners.

This mod uses **Data Models** that you can train by defeating monsters, either in-world or virtually in a
**Simulation Chamber**. Simulating a model will produce two types of matter:
- **Living Matter** (related to the dimension the mob is from), which can be crafted into various loot items
- **Pristine Matter** (unique to each Data Model type), which can be placed into a **Loot Fabricator** to produce a
  choice of loot items.

Simulation Chambers and Loot Fabricators require **Forge Energy** to operate, which is currently **not** supplied by
this mod. Both machines can be fully automated with either vanilla (hoppers) or mod-added methods, such as Thermal
Dynamics itemducts or EnderIO conduits.

Get started by making a **Deep Learner** and at least one **Data Model**. Insert the Data Model into the Deep Learner
and go kill some mobs of the required type to level the Model up to at least Basic tier. Then you can insert it
into a Simulation Chamber to start producing Matter!

### Major changes compared to the original Deep Mob Learning
- JSON configurable Data Model types and tiers, and Living Matter types
  * Data Model and Living Matter types can be added or removed
  * Data Model, Pristine Matter and Living Matter textures for added types can be supplied via
    resource pack or mods like ResourceLoader. Default fallback textures will be used if no texture files are found.
  * Recipes for data models, as well as Living Matter products, are defined in the config files
  * Data Model tiers can be added, removed and fully configured
  * See SettingsGuide.txt in the mod's config folder for more info
- Machines (Simulation Chamber and Loot Fabricator) are redstone controllable
  (always on / on with signal / off with signal / always off)
- Machines don't have restrictions on input/output sides
  * Simulation Chamber allows Data Model and Polymer Clay input from any side, and Living/Pristine Matter output
    to any side
  * Loot Fabricator allows Pristine Matter input from any side, and loot item output to any side
- Lots of under-the-hood performance improvements

### Updating from DeepMobLearning
This mod uses the same item and block IDs as the original mod, so in-place updating *should* be possible.
Always backup your world before changing mods!

### Current out-of-the-box mod support
Data Models for the following mods are defined in the default config:
- **Thermal Foundation** (Thermal Elementals, i.e. Blizz, Blitz and Basalz)
- **Twilight Forest** (four categories of mobs: Forest, Swamp, Darkwood and Glacier)
- **Tinkers' Construct** (Blue Slime)
- **Matter Overdrive** (Rogue Android)

### Planned features
- Config setting to allow reverting to legacy DML input/output side behavior</li>
- Block textures that show if a machine is currently running
- Various other small QoL improvements
- A Simulation Chamber multiblock with higher output rates (mainly meant for factory-building modpacks)

### Why no 1.15/1.16/whatever version?
I started this rewrite as a personal project for the [OmniFactory](https://www.curseforge.com/minecraft/modpacks/omnifactory)
modpack, which uses 1.12.2. Once this version is finished, I'll think about
making an 1.16 version. Anything in between <em>will be skipped</em>, so please don't ask for it.

## Experiencing issues?
Please report your issue on [GitHub](https://github.com/mustapelto/DML-Relearned) and include the following:
- A description of the bug/issue that you're experiencing
- A crash log (if applicable)