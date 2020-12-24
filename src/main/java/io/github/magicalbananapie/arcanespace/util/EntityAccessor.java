package io.github.magicalbananapie.arcanespace.util;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface EntityAccessor {
    Gravity getGravity();
    void setGravity(Entity entity, Gravity gravity);
}
