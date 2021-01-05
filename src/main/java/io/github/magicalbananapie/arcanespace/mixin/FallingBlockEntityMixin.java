package io.github.magicalbananapie.arcanespace.mixin;

import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    //This causes the blocks to all fall in the correct directions, but they move weirdly
    //Also, vertical and horizontal collisions do not work
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d add(Vec3d vec3d, double x, double y, double z) {
        ((Entity)(Object)this).velocityDirty=true;
        switch(((EntityAccessor)this).getGravity().getGravityDirection()) {
            case UP:   return vec3d.add( x, -y,  z);
            case NORTH:return vec3d.add( x,  z,  y);
            case SOUTH:return vec3d.add( x,  z, -y);
            case EAST: return vec3d.add(-y,  x,  z);
            case WEST: return vec3d.add( y,  x,  z);
            default:   return vec3d.add( x,  y,  z);
        }
    }
}
