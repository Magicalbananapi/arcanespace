package io.github.magicalbananapie.arcanespace.block;

import io.github.magicalbananapie.arcanespace.blockentity.BlockEntityGravityFocus;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

//Maybe add custom particles and sounds on block break, because this block should be a big deal...
//Also maybe rename this to gravity core and add a gravity focus block that focuses the core's gravity in one direction
//NOTICE: previous can be ignored, this block will act like a beacon or conduit and gravity will be based on surrounding blocks
public class BlockGravityFocus extends BlockWithEntity {
    protected BlockGravityFocus() { //Find a way to have it block light while being nonOpaque
        super(FabricBlockSettings.of(Material.METAL).hardness(10.0F).resistance(1200.0F).requiresTool().breakByTool(FabricToolTags.PICKAXES, 3));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityGravityFocus();
    }
}
