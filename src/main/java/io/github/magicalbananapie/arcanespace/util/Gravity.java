package io.github.magicalbananapie.arcanespace.util;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;

public class Gravity {
    private static final ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
    public static final int DEFAULT_LENGTH = 15;

    private GravityEnum defaultDirection;
    private GravityEnum temporaryDirection;
    private float gravityStrength;
    private int length; //Decrement each tick if greater than 0
    private float transitionAngle;
    private Vec3d oldEyePos;
    private Vec3d eyePosChangeVec = Vec3d.ZERO;

    //NOTICE: These 5 are questionable, among many other additions in trying to get camera rotation working
    public float prevTurnRate = 100.0F;
    public float turnRate = 100.0F;
    public float onChangeRoatDirX = 0.0F;
    public float onChangeRoatDirY = 0.0F;
    public float onChangeRoatDirZ = 0.0F;

    public Gravity() {
        final ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
        this.defaultDirection = this.temporaryDirection = GravityEnum.get(config.gravityDirection);
        this.gravityStrength = config.gravityStrength; //1F represents 100% default minecraft gravity
    }

    public void setTemporaryGravityDirection(Entity entity, int id, int length) {
        oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
        this.temporaryDirection = GravityEnum.get(id);
        this.length = length;
        gravityChangeEffects(entity, GravityEnum.get(id));
    }
    public void setTemporaryGravityDirection(Entity entity, GravityEnum newDirection, int length) {
        oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
        this.temporaryDirection = newDirection;
        this.length = length;
        gravityChangeEffects(entity, newDirection);
    }

    private void gravityChangeEffects(Entity entity, GravityEnum newDirection) {
        if (defaultDirection != newDirection) {
            this.prevTurnRate = this.turnRate = 0.0F;
            this.onChangeRoatDirX = 0.0F;
            this.onChangeRoatDirY = 0.0F;
            this.onChangeRoatDirZ = 0.0F;
            if (defaultDirection.getInverseAdjustmentFromDOWNDirection() == newDirection) {
                entity.fallDistance *= config.oppositeFallDistanceMultiplier;
            } else {
                entity.fallDistance *= config.otherFallDistanceMultiplier;
            }
        }

        if (entity.world.isClient) {
            Vec3d newEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);

            Vec3d eyesDiff = newEyePos.subtract(oldEyePos);

            setEyePosChangeVector(eyesDiff);
        }
    }

    @NotNull
    public Vec3d getEyePosChangeVector() {
        return this.eyePosChangeVec;
    }

    public void setEyePosChangeVector(@NotNull Vec3d vec3d) {
        this.eyePosChangeVec = vec3d;
    }

    public float getTransitionAngle() {
        return transitionAngle;
    }

    public void setTransitionAngle(float transitionAngle) {
        this.transitionAngle = transitionAngle;
    }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public void updateDefaultDirection() { this.defaultDirection = this.temporaryDirection; }
    public void revertGravityDirection(Entity entity) {
        oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
        this.temporaryDirection = this.defaultDirection;
        gravityChangeEffects(entity, defaultDirection);
    }

    public void setGravityDirection(Entity entity, int id) {
        oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
        this.defaultDirection = this.temporaryDirection = GravityEnum.get(id);
        gravityChangeEffects(entity, GravityEnum.get(id));
    }
    public void setGravityDirection(Entity entity, GravityEnum newDefault) {
        oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
        this.defaultDirection = this.temporaryDirection = newDefault;
        gravityChangeEffects(entity, newDefault);
    }
    public GravityEnum getGravityDirection() { return temporaryDirection; }

    public GravityEnum getDefaultDirection() { return this.defaultDirection; }
    public void setDefaultDirection(GravityEnum defaultDirection) { this.defaultDirection = defaultDirection; }

    public float getGravityStrength() { return gravityStrength; }
    public void setGravityStrength(Entity entity, float strength) { this.gravityStrength = strength; }
}

//TODO: G-Strength Variable based on y-height, such that at bedrock jumping is 1 block and world height it is 2 blocks,
// This is so game breaking that it MUST be toggleable in config
//TODO: Gravity focus is highly dependant on upcoming tinted glass and amethyst textures and therefore makes sense
// that it would incorporate them into its crafting recipe. :)
// Also because of what the gravity focus is, it should also act just like tinted glass does, so block code WIP