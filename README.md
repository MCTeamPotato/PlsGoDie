# PlsGoDie

**PlsGoDie** is a Minecraft mod that fixes an annoying issue where mobs can appear "stuck" in a death state if their health is artificially restored by other mods or code after entering the death sequence. 

Once a mob is killed, this mod ensures it truly dies, preventing visual glitches like the red overlay or tilted body effects.

---

## Features

* Forces mobs to stay dead once they enter the death sequence.
* Prevents false death animations caused by other mods or code that modify mob health.
* Configurable blacklist to exempt specific entity types (e.g., players, bosses, or modded entities).

---

## How It Works

1. Tracks mobs that enter the death sequence and records the damage source responsible.
2. Each tick, checks if a "dead" mob has been revived artificially.
3. If the mob is revived, re-applies the fatal damage to ensure proper death.
4. Blacklisted entities are ignored to prevent unintended side effects.

## Note
* config reloadable during the game by `/reload`
* May cause unexpected entity removal (hmmm not quite possible though). That's why the files are tagged as `beta`. Use at your risk. Report bugs on GitHub.