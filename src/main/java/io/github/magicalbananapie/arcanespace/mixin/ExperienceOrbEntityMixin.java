package io.github.magicalbananapie.arcanespace.mixin;

import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import io.github.magicalbananapie.arcanespace.util.GravityEnum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Seems redundant so I've removed it for now, xp orbs seem to ignore this

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    public void tick(CallbackInfo ci) {
        if(!((EntityAccessor)this).getGravity().getGravityDirection().equals(GravityEnum.DOWN))
            ((ExperienceOrbEntity)(Object)this).setVelocity(((ExperienceOrbEntity)(Object)this).getVelocity().add(0.0D, 0.03D, 0.0D));
        switch(((EntityAccessor)this).getGravity().getGravityDirection()) {
            case UP:   ((Entity)(Object)this).setVelocity(((Entity)(Object)this).getVelocity().add(0.0D, 0.03D, 0.0D));break;
            case EAST: ((Entity)(Object)this).setVelocity(((Entity)(Object)this).getVelocity().add(0.03D, 0.0D, 0.0D));break;
            case WEST: ((Entity)(Object)this).setVelocity(((Entity)(Object)this).getVelocity().add(-0.03D, 0.0D, 0.0D));break;
            case NORTH:((Entity)(Object)this).setVelocity(((Entity)(Object)this).getVelocity().add(0.0D, 0.0D, 0.03D));break;
            case SOUTH:((Entity)(Object)this).setVelocity(((Entity)(Object)this).getVelocity().add(0.0D, 0.0D, -0.03D));break;
        }
    }
}
