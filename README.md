# DML Relearned
Server friendly mob loot acquisition. A rewrite of [DeepMobLearning by xt9](https://github.com/xt9/DeepMobLearning).

Inspired by the Soul Shards mod, where you could "collect" mob kills to later use them for mob spawners.

This mod however uses "Data Models" that you can train by defeating monsters, or by placing them inside a
Simulation Chamber, which will also produce Pristine Matter of the respective type. Pristine Matter can
then be placed into a Loot Fabricator to produce its associated mob loot (configurable). Both machines require
Forge Energy to operate, and are fully automatable.

Get started by making a Deep Learner, which will house the data models to train.

## Important changes compared to the original mod
- Fully configurable Data Model types, tiers and Living Matter (JSON files) - including adding/removing models!
  * Added mob types will use a default model unless supplied
- Made machine sidedness configurable

## Updating from DeepMobLearning
This mod uses the same item and block IDs as the original mod, so in-place updating should be possible.

## Current out-of-the-box mod support
- Thermal Foundation (Thermal Elementals i.e. Blizz, Blitz and Basalz)
- Twilight Forest (four categories of mobs: Forest, Swamp, Darkwood and Glacier)
- Tinkers' Construct (Blue Slime)
- Matter Overdrive (Rogue Android)
