package io.github.magicalbananapie.arcanespace.effect;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class ArcaneEffects {

    //Potion -> StatusEffect
    //PotionEffect -> StatusEffectInstance

    public static final StatusEffect ASPHYXIATION_EFFECT = new AsphyxiationEffect();
    public static final StatusEffect FREEZING_EFFECT = new FreezingEffect();
    public static final BloodBoilEffect BLOOD_BOIL_EFFECT = new BloodBoilEffect();

    public static void registerEffects() {
        Registry.register(Registry.STATUS_EFFECT, ArcaneSpace.id("asphyxiation"),ASPHYXIATION_EFFECT);
        Registry.register(Registry.STATUS_EFFECT, ArcaneSpace.id("freezing"),FREEZING_EFFECT);
        Registry.register(Registry.STATUS_EFFECT, ArcaneSpace.id("bloodboil"),BLOOD_BOIL_EFFECT);
    }
}
