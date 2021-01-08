package io.github.magicalbananapie.arcanespace.mixin;

import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import io.github.magicalbananapie.arcanespace.util.Gravity;
import io.github.magicalbananapie.arcanespace.util.Vec3dHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static io.github.magicalbananapie.arcanespace.util.Vec3dHelper.PITCH;
import static io.github.magicalbananapie.arcanespace.util.Vec3dHelper.YAW;

//NOTICE: Mysteryem's Concerning EntityPlayerWithGravity has all of the motion stuff,
// IT WILL BE EASIER TO COME UP WITH A UNIQUE APPROACH, DO *NOT* DO THE EXACT SAME THING
//NOTICE: Mysteryem's PatchEntityPlayerSP and PathEntityPlayerSubClass also has some essential methods I was missing

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {
    @Shadow public float yaw;
    @Shadow public float pitch;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;
    @Shadow private Entity vehicle;
    @Shadow private EntityDimensions dimensions;
    @Shadow public abstract void setBoundingBox(Box boundingBox);
    @Shadow public abstract void setPos(double x, double y, double z);
    @Shadow protected abstract Box getBoundingBox();
    @Shadow @Final private EntityType<?> type;

    //TODO: FromTag, ToTag,

    @Shadow private Vec3d pos;
    //Direction defaults to DOWN if not set correctly, should help compatibility and reduce bugs the tiniest bit.
    // Also, Gravity Strength 0 is Zero-G: can't fall, jump, or move by walking and must use items to move.
    // Negative gravity strength will push away affected entities without changing direction;
    // This is functionally identical as far as physics go, but entity and model rotation is opposite.
    private Gravity gravity = new Gravity();



    @Override
    public Gravity getGravity() { return gravity; }

    @Override
    public void setGravity(Entity entity, Gravity gravity) { this.gravity = gravity; }

    //Voodoo that probably does nothing ;-;
    /*@Redirect(method = "updatePosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityDimensions;method_30231(DDD)Lnet/minecraft/util/math/Box;"))
    public Box updatePosition(EntityDimensions entityDimensions, double x, double y, double z) {
        return this.getGravity().getGravityDirection().getGravityAdjustedAABB((Entity)(Object)this);
    }*/

    /**
     * @author Magicalbananapie
     * Why am I doing this, you ask? Well, the Up&Down&AllAround Mod Author, Mysteryem,
     * created a new type of playerEntity to overwrite this same method, and as of now,
     * changing this method is nessesary to make sure the bounding box is in the correct
     * place for each gravity direction, I felt like this was a better solution that was unlikely to
     * cause many conflicts, but I can
     */
    @Redirect(method = "moveToBoundingBoxCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPos(DDD)V"))
    public void moveToBoundingBoxCenter(Entity entity, double x, double y, double z) {
        ((EntityAccessor)this).getGravity().getGravityDirection().resetPositionToBB((Entity)(Object)this);
    }

    @Inject(method = "updatePosition", at = @At(value = "TAIL"))
    public void updatePosition(double x, double y, double z, CallbackInfo ci) {
        setBoundingBox(this.getGravity().getGravityDirection().getOpposite().getGravityAdjustedAABB((Entity)(Object)this));
    }

    //@Redirect(method = "calculateDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setBoundingBox(Lnet/minecraft/util/math/Box;)V", ordinal = 0))
    //public void setBB(Entity entity, Box boundingBox) {}

    /**
     * Changes getEyeHeight to account for gravity... theoretically
     *
     * @author Magicalbananapie
     * @param pose EntityPose
     * @param dimensions EntityDimensions
     */
    @Inject(method = "getEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F", at = @At(value = "HEAD"), cancellable = true)
    public void getEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        switch(gravity.getGravityDirection()) {
            default: cir.setReturnValue(0F);
            case UP: cir.setReturnValue(-dimensions.height * 0.85F);
            case DOWN: cir.setReturnValue(dimensions.height * 0.85F);
        }
    }

    /**
     * Changes Dimensions to account for gravity... theoretically
     *
     * @author Magicalbananapie
     * @param pose EntityPose
     */
    @Inject(method = "getDimensions", at = @At(value = "HEAD"), cancellable = true)
    public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        switch(gravity.getGravityDirection()) {
            case UP: cir.setReturnValue(this.type.getDimensions().scaled(0.5F));
            case DOWN: cir.setReturnValue(this.type.getDimensions().scaled(0.5F));
            default: cir.setReturnValue(this.type.getDimensions().scaled(0.5F));
        }
    }

    @Inject(method = "calculateDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setBoundingBox(Lnet/minecraft/util/math/Box;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setBB(CallbackInfo ci, EntityDimensions entityDimensions, EntityPose entityPose, EntityDimensions entityDimensions2, Box box) {
        double heightExpansion = (this.dimensions.height - entityDimensions.height) / 2d;
        double widthExpansion = (this.dimensions.width - entityDimensions.width) / 2d;
        double[] d = this.gravity.getGravityDirection().adjustXYZValuesMaintainSigns(widthExpansion, heightExpansion, widthExpansion);
        double[] d2 = this.gravity.getGravityDirection().adjustXYZValues(0, heightExpansion, 0);
        this.setBoundingBox(box.expand(d[0], d[1], d[2]).offset(d2[0], d2[1], d2[2]));
    }

    //IDK WHAT TO DO HERE AAAHHHHHHHHHH, THIS IS DEFINITELY GOING TO BREAK SOMETHING
    @Redirect(method = "calculateDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setBoundingBox(Lnet/minecraft/util/math/Box;)V"/*, ordinal = 1*/))
    public void setBB(Entity entity, Box boundingBox) {/*DO NOTHING*/}

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