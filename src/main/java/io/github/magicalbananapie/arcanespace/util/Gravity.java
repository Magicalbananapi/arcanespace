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
    public static final int DEFAULT_LENGTH = 20;
    public static final int DEFAULT_TIMEOUT = 15; //Ticks before rotation can happen again

    //FIXME: I made a LOT of mistakes setting up gravityDirection, this is going to take some time to fix
    private final GravityEnum defaultDirection; //TODO: Make this dimension specific, AKA make dimensions store this
    private GravityEnum previousDirection, currentDirection;
    private float gravityStrength;
    private int timeout; //Decrement each tick if greater than 0
    private int length; //Can be combined with timeout if made to be timeout*4/3
    private float transitionAngle;
    private boolean hasTransitionAngle = false; //Can be removed by checking if transitionAngle is null
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
        this.previousDirection = this.currentDirection = this.defaultDirection  = GravityEnum.get(config.gravityDirection);
        this.gravityStrength = config.gravityStrength; //1F represents 100% default minecraft gravity
    }

    public void setGravityDirection(Entity entity, int id, boolean override) {
        setGravityDirection(entity, GravityEnum.get(id),  override);
    }
    public void setGravityDirection(Entity entity, GravityEnum newDirection, boolean override) {
        if(override||!newDirection.equals(this.currentDirection)&&this.timeout<=0&&this.length<=0) {
            this.oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
            this.previousDirection = this.currentDirection;
            this.currentDirection = newDirection;
            this.timeout = DEFAULT_TIMEOUT;
            this.length = DEFAULT_LENGTH;
            gravityChangeEffects(entity, newDirection);
        } else if(newDirection.equals(this.currentDirection)) this.length = DEFAULT_LENGTH;
    }

    public boolean hasTransitionAngle() { return this.hasTransitionAngle; }

    private void gravityChangeEffects(Entity entity, GravityEnum newDirection) {
        this.transitionAngle = 0;
        this.hasTransitionAngle = false;
        if (this.previousDirection != newDirection) {
            this.prevTurnRate = this.turnRate = 0.0F;
            this.onChangeRoatDirX = 0.0F;
            this.onChangeRoatDirY = 0.0F;
            this.onChangeRoatDirZ = 0.0F;
            if (this.previousDirection.getOpposite() == newDirection)
                entity.fallDistance *= config.oppositeFallDistanceMultiplier;
            else entity.fallDistance *= config.otherFallDistanceMultiplier;
        }

        if (entity.world.isClient) {
            Vec3d newEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
            Vec3d eyesDiff = newEyePos.subtract(this.oldEyePos);
            setEyePosChangeVector(eyesDiff);
        }
    }

    @NotNull public Vec3d getEyePosChangeVector() { return this.eyePosChangeVec; }
    public void setEyePosChangeVector(@NotNull Vec3d vec3d) { this.eyePosChangeVec = vec3d; }

    public int getTimeout() { return this.timeout; }
    public void tickTimeout() { --this.timeout; }

    public int getLength() { return this.length; }
    public void tickLength() { --this.length; }

    public float getTransitionAngle() { return this.transitionAngle; }
    public void setTransitionAngle(float transitionAngle) {
        this.transitionAngle = transitionAngle;
        this.hasTransitionAngle = true;
    }

    public void revertGravityDirection(Entity entity) {
        this.oldEyePos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
        this.currentDirection = this.defaultDirection;
        gravityChangeEffects(entity, this.defaultDirection);
    }
    public GravityEnum getPreviousDirection() { return this.previousDirection; }
    public GravityEnum getGravityDirection() { return this.currentDirection; }
    public GravityEnum getDefaultDirection() { return this.defaultDirection; }

    public float getGravityStrength() { return this.gravityStrength; }
    public void setGravityStrength(Entity entity, float strength) { this.gravityStrength = strength; }
}

//TODO: G-Strength Variable based on y-height, such that at bedrock jumping is 1 block and world height it is 2 blocks,
// This is so game breaking that it MUST be toggleable in config
//TODO: Gravity focus is highly dependant on upcoming tinted glass and amethyst textures and therefore makes sense
// that it would incorporate them into its crafting recipe. :)
// Also because of what the gravity focus is, it should also act just like tinted glass does, so block code WIP,
// It can only be completely completed in 1.17 anyways so I can wait until I move to that version