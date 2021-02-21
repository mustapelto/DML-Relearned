## This mod is currently in beta!
This means **there will probably be bugs!** Do not use this on an important world that you don't have a backup of. **You have been warned!**

# Deep Mob Learning: Relearned
This mod is a complete rewrite from scratch of [DeepMobLearning](https://www.curseforge.com/minecraft/mc-mods/deep-mob-learning)
by xt9/IterationFunk. The purpose of this rewrite was to add several new features (see below) that would have been difficult,
if not impossible to implement as a modification of the original.

Most of the original textures (also by xt9) have been reused, though mostly modified in some way.

Making this mod would not have been possible without all the work that went into the original!

### What this mod does (adapted from the original mod's description)
A server-friendly and peaceful-compatible mod for mob loot acquisition. Originally inspired by the Soul Shards mod,
where you could "collect" mob kills to later use them for mob spawners.

This mod uses **Data Models** that you can train by defeating monsters, either in-world or virtually in a
**Simulation Chamber**. Simulating a model will additionally produce two types of matter:
- **Living Matter** (related to the dimension the mob is from), which can be crafted into various loot items
- **Pristine Matter** (unique to each Data Model type), which can be placed into a **Loot Fabricator** to produce a
  choice of loot items. Higher tier models produce more of this matter type.

Simulation Chambers and Loot Fabricators require **Forge Energy** to operate, which is currently **not** supplied by
this mod. Both machines can be fully automated with either vanilla (hoppers) or mod-added methods, such as Thermal
Expansion itemducts or EnderIO conduits.

Get started by making a **Deep Learner** and at least one **Data Model**. Insert the Data Model into the Deep Learner
and go kill some mobs of the required type to level the Model up to at least Basic tier. Then you can insert it
into a Simulation Chamber to start producing Matter!

### Changes compared to the original Deep Mob Learning
- Fully JSON configurable Data Model types, tiers, and Living Matter types
    * New types/tiers can be added or existing ones changed/removed
    * Data Model, Pristine Matter and Living Matter textures for added types can be supplied by a resource pack or
      through mods like ResourceLoader. Default fallback textures will be used if no matching texture files are found.
    * Recipes for data models, as well as Living Matter products, are defined in the config files
    * Data Model tiers can be added, removed and fully configured
    * See SettingsGuide.txt in the mod's config folder for more info
- Machines are redstone controllable (always on / on with signal / off with signal / always off)
- Machines don't have restrictions on input/output sides
    * Simulation Chamber allows Data Model and Polymer Clay input from any side, and Living/Pristine Matter output
      to any side
    * Loot Fabricator allows Pristine Matter input from any side, and loot item output to any side
    * A config setting is available to revert to the original DML behavior
- Machine blocks change appearance based on the current state of crafting (idle / running / error)
- Several minor QoL improvements
- Several under-the-hood performance improvements

*Users of [Deep Mob Learning - Blood Magic Addon](https://www.curseforge.com/minecraft/mc-mods/deep-mob-learning-blood-magic-addon):*
The addon is not compatible with this mod. I haven't decided yet whether I'll make a new version of the addon. Unfortunately,
this means that you won't be able to use this mod for the time being.


### Updating from DeepMobLearning
This mod uses the same item and block registry names as the original, so in-place updating should be possible as follows:
- Always backup your world before changing mods!
- Add the new mod jar and remove the old one
- Run Minecraft once (don't open your world yet) to generate the new config files (in config/dml_relearned/)
- Manually copy any changes you made to the original config (config/deepmoblearning.cfg) into the new files
- The old config file can be deleted, it's not used by this mod
- Restart Minecraft, load your world, and re-set all your Loot Fabricators' outputs once (this is necessary
  because of internal changes in how the setting is stored on disk)
- Enjoy!

### Current out-of-the-box mod support
Data Models for the following mods are defined in the default config:
- **Thermal Foundation** (one combined Data Model for Thermal Elementals, i.e. Blizz, Blitz and Basalz)
- **Twilight Forest** (four categories of mobs: Forest, Swamp, Darkwood and Glacier)
- **Tinkers' Construct** (Blue Slime)
- **Matter Overdrive** (Rogue Android)

### Why no 1.15/1.16/whatever version?
This rewrite was inspired by and originally intended for the [OmniFactory](https://www.curseforge.com/minecraft/modpacks/omnifactory)
modpack, which uses 1.12.2. Once this version is finished, I'm planning to make a 1.16 version.
I will *not* make 1.13/1.14/1.15 versions, so please don't ask for those.

## Experiencing issues?
Please report your issue on [GitHub](https://github.com/mustapelto/DML-Relearned), including
- a description of the bug/issue that you're experiencing
- a crash log (if applicable)