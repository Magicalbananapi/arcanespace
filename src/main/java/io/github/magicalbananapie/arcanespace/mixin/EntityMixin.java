package io.github.magicalbananapie.arcanespace.mixin;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import io.github.magicalbananapie.arcanespace.util.Gravity;
import io.github.magicalbananapie.arcanespace.util.GravityEnum;
import io.github.magicalbananapie.arcanespace.util.Vec3dHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.magicalbananapie.arcanespace.util.Vec3dHelper.PITCH;
import static io.github.magicalbananapie.arcanespace.util.Vec3dHelper.YAW;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {
    @Shadow public float yaw;
    @Shadow public float pitch;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;
    @Shadow private Entity vehicle;
    @Shadow public abstract void setNoGravity(boolean noGravity);
    //TODO: FromTag, ToTag,

    //Direction defaults to DOWN if not set correctly, should help compatibility and reduce bugs the tiniest bit.
    // Also, Gravity Strength 0 is Zero-G: can't fall, jump, or move by walking and must use items to move.
    // Negative gravity strength will push away affected entities without changing direction;
    // This is functionally identical as far as physics go, but entity and model rotation is opposite.
    private Gravity gravity = new Gravity();

    @Override
    public Gravity getGravity() { return gravity; }

    @Override
    public void setGravity(Entity entity, Gravity gravity) { this.gravity = gravity; }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickNetherPortal()V"))
    private void baseTick(CallbackInfo ci) {
        if(this.gravity.getTimeout() > 0) this.gravity.tickTimeout();
        if(this.gravity.getLength() > 0) this.gravity.tickLength();
        else this.gravity.revertGravityDirection((Entity)(Object)this);
    }

    //@Shadow public void method_30634(double x, double y, double z) {}

    /**
     * @author Magicalbananapie
     * - I'm theoretically using this to sync mouse and camera
     * - Seems to half-break inverted mouse setting :(
     */
    @Environment(EnvType.CLIENT)
    @Overwrite public void changeLookDirection(double yaw, double pitch) {
        final double[] relativePitchYaw = Vec3dHelper.getPrecisePitchAndYawFromVector(
                this.getGravity().getGravityDirection().getOpposite().adjustLookVec(
                        Vec3dHelper.getPreciseVectorForRotation(this.pitch, this.yaw)));

        final double changedRelativeYaw = relativePitchYaw[YAW] + (yaw * 0.15d);
        final double changedRelativePitch = relativePitchYaw[PITCH] + (pitch * 0.15d); //ended up removing negative on pitch

        final double maxRelativeYaw = 89.99d;

        double clampedRelativePitch;
        if (changedRelativePitch > maxRelativeYaw) clampedRelativePitch = maxRelativeYaw;
        else clampedRelativePitch = Math.max(changedRelativePitch, -maxRelativeYaw);

        // Directly set pitch and yaw
        final double[] absolutePitchYaw = Vec3dHelper.getPrecisePitchAndYawFromVector(
                this.getGravity().getGravityDirection().adjustLookVec(
                        Vec3dHelper.getPreciseVectorForRotation(clampedRelativePitch, changedRelativeYaw)));

        final double changedAbsolutePitch = absolutePitchYaw[PITCH];
        final double changedAbsoluteYaw = (absolutePitchYaw[YAW] % 360);

        // Yaw calculated through yaw change
        final float absoluteYawChange;
        final double effectiveStartingAbsoluteYaw = this.yaw % 360;

        // Limit the change in yaw to 180 degrees each tick
        if (Math.abs(effectiveStartingAbsoluteYaw - changedAbsoluteYaw) > 180) { //(maxRelativeYaw * 2) instead of 180?
            if (effectiveStartingAbsoluteYaw < changedAbsoluteYaw)
                absoluteYawChange = (float)(changedAbsoluteYaw - (effectiveStartingAbsoluteYaw + 360));
            else absoluteYawChange = (float)((changedAbsoluteYaw + 360) - effectiveStartingAbsoluteYaw);
        } else absoluteYawChange = (float)(changedAbsoluteYaw - effectiveStartingAbsoluteYaw);
        float pitchParam = (float)(this.pitch - changedAbsolutePitch);

        this.pitch -= pitchParam; //... also added negative here for consistency
        this.yaw += absoluteYawChange;
        this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
        this.prevPitch -= pitchParam; //TODO: Figure out if I should be subtracting here (Or rather if this is causing any issues)
        this.prevYaw += absoluteYawChange;
        this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
        if (this.vehicle != null) this.vehicle.onPassengerLookAround((Entity)(Object)this);
    }

    //NOTICE: Call below in whatever place checks data.
    // if(config.gravityStrength==0) setNoGravity(true);

    //TODO: Check for baseTick(), tick(), tickMovement(), and all similar tick methods that may effect gravity :)

//NOTICE: These are livingEntity only and with the transition to Entity, these are things to look out for.
    //TODO: LivingEntity's travel() looks useful
    //TODO: LivingEntity's tickMovement() is helpful, leads to tickNewAi() if not immobile, also all
    // the unmapped and swimming methods AND the jump() method, AND initAI for specific entities. :)
}