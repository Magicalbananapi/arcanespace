package io.github.magicalbananapie.arcanespace;

import io.github.magicalbananapie.arcanespace.util.Gravity;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

//VERY WIP, need to add categories and collapsable rotation config, and in general redo the entire thing

@Config(name = ArcaneSpace.MOD_ID)
public class ArcaneConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.BoundedDiscrete(min = 0, max = (long)1d)
    public float oppositeFallDistanceMultiplier = 0;

    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.BoundedDiscrete(min = 0, max = (long)1d)
    public float otherFallDistanceMultiplier = (float)0.5d;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean transitionEnabled = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.BoundedDiscrete(min = 0, max = (long)1000d)
    public double rotationAnimationSpeed = 1.5; //transition speed, 30 ticks

    @ConfigEntry.Gui.Tooltip
    public double rotationAnimationLength = Gravity.DEFAULT_TIMEOUT / rotationAnimationSpeed;

    @ConfigEntry.Gui.Tooltip
    public double rotationAnimationEnd = Gravity.DEFAULT_TIMEOUT - rotationAnimationLength;

    @ConfigEntry.Gui.Tooltip
    public int gFieldSize = 8;

    //Planetoids should be added to a space dimension, each with their own gravity focus in the center, gen config needed
    //Maybe use structures to generate rare planetoids that have either prebuilt features, dungeons, or complex gravity fields,
    //Also, apparently the portal testing chambers ARE in space :) [I can't wait until I can start work on stuff like this]

    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.CollapsibleObject
    public VoidConfig voidConfig = new VoidConfig();

    public static class VoidConfig {

        @ConfigEntry.Gui.Tooltip(count = 2)
        public double noAirHeight = 257;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean bubblesAidAsphyxiation = false;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public double freezingHeight = 356;
        //TODO: should happen in stages, with 1.17 freezing then the implemented freezing after a lot of polish

        @ConfigEntry.Gui.Tooltip(count = 2)
        public double bloodBoilHeight = 500;

        @ConfigEntry.Gui.Tooltip
        public double instantDeathHeight = 700;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public int ticksUntilFullyFrozen = 100;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public float bloodBoilDamage = (float)4d;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean spaceHurtsCreative = true;
    }

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int gravityDirection = 0;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public float gravityStrength = 1.0F;

    @ConfigEntry.Gui.Tooltip(count = 4)
    @ConfigEntry.Gui.CollapsibleObject
    public VisualConfig visualConfig = new VisualConfig();

    public static class VisualConfig {
        @ConfigEntry.Gui.Tooltip(count = 3)
        public boolean sphereEnabled = false;

        @ConfigEntry.Gui.Tooltip(count = 4)
        public int sphereComplexity = 6;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean glowEnabled = true;
        //This should be changed to be a button like difficulty or graphics later on to include rarity glow

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean rarityGlow = true;

        @ConfigEntry.Gui.Tooltip
        public boolean pixelatedGlow = false;
    }

    @ConfigEntry.Gui.Tooltip
    public boolean debugMode = false;
}