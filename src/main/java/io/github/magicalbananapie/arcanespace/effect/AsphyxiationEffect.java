package io.github.magicalbananapie.arcanespace.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class AsphyxiationEffect extends StatusEffect {
    public AsphyxiationEffect() {
        super(StatusEffectType.HARMFUL,1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity livingEntity, int amplifier) {/*OVERRIDE TO DO NOTHING AS HEALS BY DEFAULT*/}
}
