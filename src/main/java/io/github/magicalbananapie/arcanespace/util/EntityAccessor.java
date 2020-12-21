package io.github.magicalbananapie.arcanespace.util;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface EntityAccessor {
    Gravity getGravity();
    void setGravity(Entity entity, Gravity gravity);
    void setGravityDirection(Entity entity, GravityEnum gravityDirection);
    void setTemporaryDirection(Entity entity, GravityEnum gravityDirection, int length);
    void setGravityStrength(Entity entity, float gravityStrength);
}
