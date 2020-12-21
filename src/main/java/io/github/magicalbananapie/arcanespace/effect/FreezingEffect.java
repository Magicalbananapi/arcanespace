package io.github.magicalbananapie.arcanespace.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class FreezingEffect extends StatusEffect {
    public FreezingEffect() {
        super(StatusEffectType.HARMFUL,1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity livingEntity, int amplifier) {/*DO NOTHING*/}
}
