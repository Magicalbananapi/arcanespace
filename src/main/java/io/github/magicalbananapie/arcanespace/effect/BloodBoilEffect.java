package io.github.magicalbananapie.arcanespace.effect;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.util.ArcaneDamageSource;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class BloodBoilEffect extends StatusEffect {
    ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();

    public BloodBoilEffect() {
        super(StatusEffectType.HARMFUL,1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity livingEntity, int amplifier) {
        if(config.voidConfig.spaceHurtsCreative) livingEntity.damage(ArcaneDamageSource.BLOOD_BOIL, config.voidConfig.bloodBoilDamage);
        else livingEntity.damage(ArcaneDamageSource.BLOOD_BOIL_S, config.voidConfig.bloodBoilDamage);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) { return true; }
}
