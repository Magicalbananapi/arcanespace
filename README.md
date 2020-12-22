# arcanespace
A Minecraft magic mod for Fabric, it intends to recreate Starminer-styled gravity, or Gravity Direction, in conjunction
with Galacticraft-styled gravity, or Gravity Strength, in the form of a magic mod based around spatial manipulation.

This is my first Minecraft mod discounting early attempts years ago with MCreator and I don't have a complete idea for the mod yet.
As such I might completely change the direction of it later on and absolutely nothing is final, so suggestions are welcome.

I should note I aim to make this mod feel relatively vanilla-like in it's default state, so things like gravity direction 
will stay limited to the 6 directions supported by vanilla minecraft or visuals will use blocky models by default, although the config
might have options to enable less vanilla-like features like the spherical singularities I currently have implemented.

A good number of the current and future features were ported from Up and Down and All Around by Mysteryem and Starminer by (Japanese author, minecraft forums says it's あるべ, but the japanese forum is down so I can't fact check it)

Currently Implemented:
- Gravity stored on entities (Until World Reload)
- Camera rotates based on gravity
- Gravity Panels (Ported a similar item From Starminer and U&D&AA)
- Gravity Focus

Currently Working On:
- Make entites actually effected by different gravity directions (AKA falling up or sideways)
- Rotating Entity Models under the effect of gravity (This should fix )
- Rotating movement and anything remaining (If not fixed by prior steps)
- Saving and Loading Gravity to entity
- Set Gravity on world load if Gravity doesn't exist (Only if not invalidated by previous step).
- Implement Camera transitions (Might require changes from)
- Implementing Gravity Strength
- Implement G-Fields (A region that changes an entity's Gravity when entered and based on relative location to a focal point)
- Implement Gravity Focus Functionality (Acts like a beacon or conduit to emit a g-field)

Future Plans(Once the previously mentioned steps are completed):
- Remove redundancies in code and rewrite some of it
- Make project file structure and naming conventions more consistant
- Improve renderers and create new ones to partially mirror enchantment glint
- Implement alternate form of enchanting using new glint (Look into entity reach modifiers for this)
- Work on parts of the magic system unrelated to gravity or at least develop the idea for the magic system
- Look into increasing performance and rewriting again
- Seperate Gravity into a Library to make it easier to use in other mods(of mine and other people's like FluxTech)

Up and Down and All Around
Starminer
