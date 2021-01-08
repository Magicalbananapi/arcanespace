package io.github.magicalbananapie.arcanespace.mixin;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.effect.ArcaneEffects;
import io.github.magicalbananapie.arcanespace.util.ArcaneDamageSource;
import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import io.github.magicalbananapie.arcanespace.util.LivingEntityAccessor;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccessor {
    private final ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();

    //this.setVelocity(this.applyClimbingSpeed(this.getVelocity())); to change how climbing works at least partially

    @Unique public int freezeTime;
    @Unique @Override public int getFreezeTime() { return freezeTime; }
    @Unique @Override public void setFreezeTime(int freezeTime) { this.freezeTime = freezeTime; }

    /*@Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d add(Vec3d vec3d, double x, double y, double z) {
        //((Entity)(Object)this).velocityDirty=true;
        switch(((EntityAccessor)this).getGravity().getGravityDirection()) {
            case UP:   return vec3d.add( x, -y,  z);
            case NORTH:return vec3d.add( x,  z,  y);
            case SOUTH:return vec3d.add( x,  z, -y);
            case EAST: return vec3d.add(-y,  x,  z);
            case WEST: return vec3d.add( y,  x,  z);
            default:   return vec3d.add( x,  y,  z);
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 2))
    public void setVelocity(LivingEntity livingEntity, double x, double y, double z) {
        //((Entity)(Object)this).velocityDirty=true;
        switch(((EntityAccessor)this).getGravity().getGravityDirection()) {
            case UP:   livingEntity.setVelocity( x, -y,  z);
            case NORTH:livingEntity.setVelocity( x,  z,  y);
            case SOUTH:livingEntity.setVelocity( x,  z, -y);
            case EAST: livingEntity.setVelocity(-y,  x,  z);
            case WEST: livingEntity.setVelocity( y,  x,  z);
            default:   livingEntity.setVelocity( x,  y,  z);
        }
    }*/

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isFireImmune()Z"))
    private void baseTick(CallbackInfo ci) {
        //TODO: MOVE SPACE DAMAGE TO ENTITY MIXIN INSTEAD OF LIVING ENTITY MIXIN TO MAKE IT MIRROR VOID DAMAGE
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {

            if (entity.hasStatusEffect(ArcaneEffects.FREEZING_EFFECT) && freezeTime < config.voidConfig.ticksUntilFullyFrozen)
                freezeTime += 1;
            else if(freezeTime > 0)
                freezeTime -= 1;

        } else {
            double posY = entity.getY();
            if (posY > config.voidConfig.noAirHeight) {
                entity.addStatusEffect(new StatusEffectInstance(ArcaneEffects.ASPHYXIATION_EFFECT, 5, 0, false, false));
            }

            if (posY > config.voidConfig.freezingHeight) {
                entity.addStatusEffect(new StatusEffectInstance(ArcaneEffects.FREEZING_EFFECT, 5, 0, false, false));
            }

            if (posY > config.voidConfig.bloodBoilHeight) {
                entity.addStatusEffect(new StatusEffectInstance(ArcaneEffects.BLOOD_BOIL_EFFECT, 5, 0, false, false));
            }

            if (posY > config.voidConfig.instantDeathHeight) {
                if(config.voidConfig.spaceHurtsCreative) entity.damage(ArcaneDamageSource.OUTERSPACE, Float.MAX_VALUE);
                else entity.damage(ArcaneDamageSource.OUTERSPACE_S, Float.MAX_VALUE);
            }
        }
    }

    //TODO: Go over all below again, because earlier I broke drowning to an unknown extent by not defaulting to
    // the vanilla logic when asphyxiation potion effect is not detected

    // Asphyxiation logic
    @Redirect(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z"))
    private boolean baseTick(LivingEntity livingEntity, Tag<Fluid> tag) {
        return (livingEntity.isSubmergedIn(tag)&&!livingEntity.world.getBlockState(new BlockPos(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ())).isOf(Blocks.BUBBLE_COLUMN))||livingEntity.hasStatusEffect(ArcaneEffects.ASPHYXIATION_EFFECT);
    }

    @Redirect(method = "baseTick()V", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState baseTick(World world, BlockPos pos) {
        if(config.voidConfig.bubblesAidAsphyxiation) return world.getBlockState(pos);
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        return (livingEntity.hasStatusEffect(ArcaneEffects.ASPHYXIATION_EFFECT)?new BlockState(Blocks.VOID_AIR, null, null):world.getBlockState(pos));
    }

    // I don't need to replace other part of if statement because it only affects undead,
    // and I'm ok with undead not drowning in space

    @Redirect(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasWaterBreathing(Lnet/minecraft/entity/LivingEntity;)Z"))
    private boolean baseTick(LivingEntity entity) {
        return StatusEffectUtil.hasWaterBreathing(entity)&&!entity.hasStatusEffect(ArcaneEffects.ASPHYXIATION_EFFECT);
    }

    /*@Redirect(method = "baseTick()V", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void baseTick(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        TODO: CHANGE PARTICLES WHEN ASPHYXIATED: LOW PRIORITY AND AFTER GRAVITY, AKA Quality of Life
    }*/

    // drown or asphyxiate, pick one, not both
    @ModifyVariable(method = "damage", at = @At(value = "HEAD"))
    private DamageSource baseTick(DamageSource source) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        return (livingEntity.hasStatusEffect(ArcaneEffects.ASPHYXIATION_EFFECT)&&source==DamageSource.DROWN)?ArcaneDamageSource.ASPHYXIATION:/*Changing source will change all attack types, 1 mistake and everything drowns you or can kill creative players*/source;
    }
}
