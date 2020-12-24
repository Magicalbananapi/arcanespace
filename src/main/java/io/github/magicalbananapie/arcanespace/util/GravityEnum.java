package io.github.magicalbananapie.arcanespace.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public enum GravityEnum {
    //Mirrors game mode Enum and name used for commands
    DOWN (0, "down", new Vec3i(0, 0, 0), 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F){
        @Override public double[] adjustXYZValues(double x, double y, double z) {
            return new double[]{x, y, z};
        }
        @Override public GravityEnum getOpposite() {
            return this;
        } //Up and Down shouldn't be changed
    },
    UP   (1, "up", new Vec3i(0, 0, 180), 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F){
        @Override public double[] adjustXYZValues(double x, double y, double z) { return new double[]{-x, -y, z}; }
        @Override public GravityEnum getOpposite() { return this; } //Up and Down shouldn't be changed
    },
    NORTH(2, "north", new Vec3i(90, 0, 0),1.0F, 0.0F, 0.0F, 0.0F, 1.0F, -0.5F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, 1.0F){
        @Override public double[] adjustXYZValues(double x, double y, double z) { return new double[]{x, -z, y}; }
        @Override public GravityEnum getOpposite() { return SOUTH; }
    },
    SOUTH(3, "south", new Vec3i(-90, 0, 0),1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F){
        @Override public double[] adjustXYZValues(double x, double y, double z) { return new double[]{x, z, -y}; }
        @Override public GravityEnum getOpposite() { return NORTH; }
    },
    EAST (4, "east",  new Vec3i(0, 0, 90),0.0F, 1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.5F, 1.0F, 1.0F, 0.0F, -1.0F, 0.0F, 0.0F){
        @Override public double[] adjustXYZValues(double x, double y, double z) { return new double[]{-y, x, z}; }
        @Override public GravityEnum getOpposite() { return WEST; }
    },
    WEST (5, "west",  new Vec3i(0, 0, -90),0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.5F, -1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F){
        @Override public double[] adjustXYZValues(double x, double y, double z) { return new double[]{y, -x, z}; }
        @Override public GravityEnum getOpposite() { return EAST; }
    };

    private final int id;
    private final String name;
    private final Vec3i cameraTransformVars;
    public float pitchRotDirX;
    public float pitchRotDirY;
    public float yawRotDirX;
    public float yawRotDirY;
    public float yawRotDirZ;
    public float rotX;
    public float rotZ;
    public float shiftEyeX;
    public float shiftEyeY;
    public float shiftEyeZ;
    public float shiftSneakX;
    public float shiftSneakY;
    public float shiftSneakZ;

    GravityEnum(int id, String name, Vec3i cameraTransformVars, float argPitchRotDirX, float argPitchRotDirY, float argYawRotDirX, float argYawRotDirY, float argYawRotDirZ, float argRotX, float argRotZ, float argShiftEyeX, float argShiftEyeY, float argShiftEyeZ, float argShiftSneakX, float argShiftSneakY, float argShiftSneakZ) {
        this.id = id;
        this.name = name;
        this.cameraTransformVars = cameraTransformVars;
        this.pitchRotDirX = argPitchRotDirX;
        this.pitchRotDirY = argPitchRotDirY;
        this.yawRotDirX = argYawRotDirX;
        this.yawRotDirY = argYawRotDirY;
        this.yawRotDirZ = argYawRotDirZ;
        this.rotX = argRotX;
        this.rotZ = argRotZ;
        this.shiftEyeX = argShiftEyeX;
        this.shiftEyeY = argShiftEyeY;
        this.shiftEyeZ = argShiftEyeZ;
        this.shiftSneakX = argShiftSneakX;
        this.shiftSneakY = argShiftSneakY;
        this.shiftSneakZ = argShiftSneakZ;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Vec3i getCameraTransformVars() { return cameraTransformVars; }

    public abstract GravityEnum getOpposite();

    public Vec3d adjustLookVec(Vec3d input) {
        double[] d = this.adjustXYZValues(input.x, input.y, input.z);
        return new Vec3d(d[0], d[1], d[2]);
    }

    public abstract double[] adjustXYZValues(double x, double y, double z);

    public Text getTranslatableName() {
        return new TranslatableText("gravity." + this.name);
    }

    public static GravityEnum get(int id) {
        if     (id==1) return GravityEnum.UP;
        else if(id==2) return GravityEnum.NORTH;
        else if(id==3) return GravityEnum.SOUTH;
        else if(id==4) return GravityEnum.EAST;
        else if(id==5) return GravityEnum.WEST;
        else  /*id==0*/return GravityEnum.DOWN; //Defaults to Down
    }
}