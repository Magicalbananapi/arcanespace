package io.github.magicalbananapie.arcanespace.blockentity;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.block.ArcaneBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ArcaneBlockEntities {
    //NOTICE: For some reason the BlockEntityType being defined specifically was VERY important to the renderer
    public static final BlockEntityType<BlockEntityGravityFocus> GRAVITY_FOCUS = BlockEntityType.Builder.create(BlockEntityGravityFocus::new, ArcaneBlocks.GRAVITY_FOCUS).build(null);

    public static void registerBlockEntities() {
        register("gravity_focus", GRAVITY_FOCUS);
    }

    public static void register(String name, BlockEntityType<? extends BlockEntity> blockEntity) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ArcaneSpace.id(name), blockEntity);
    }
}