package io.github.magicalbananapie.arcanespace.block;

import io.github.magicalbananapie.arcanespace.util.Gravity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import net.minecraft.state.StateManager.Builder;

public class BlockGravityPanel extends FacingBlock implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final float THICKNESS = 0.125F;

    //NOTICE: I JUST REALIZED IT ACTS LIKE A SOLID AS IT CUTS OUT A FACE WHEN A BLOCK IS ADJACENT, THIS SHOULD NOT
    // HAPPEN AND HINTS TOWARDS THE ANSWER TOWARDS MY OTHER PROBLEM

    //FIXME: These are supposed to act kind of like glass where the inner sides of connecting blocks aren't visible,
    // however this isn't happening so I need to figure out why (Even when Material type is GLASS)
    public BlockGravityPanel() {
        super(FabricBlockSettings.of(Material.GLASS).nonOpaque().strength(5.0F,9.0F).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1));
        setDefaultState(getDefaultState().with(FACING, Direction.UP).with(WATERLOGGED, false)); //Starts Facing UP, but with DOWN gravity
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)) {
            case UP: return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, 1.0F, THICKNESS, 1.0F);
            case DOWN: return VoxelShapes.cuboid(0.0F, 1.0F - THICKNESS, 0.0F, 1.0F, 1.0F, 1.0F);
            case NORTH: return VoxelShapes.cuboid(0.0F, 0.0F, 1.0F - THICKNESS, 1.0F, 1.0F, 1.0F);
            case EAST: return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, THICKNESS, 1.0F, 1.0F);
            case SOUTH: return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, THICKNESS);
            case WEST: return VoxelShapes.cuboid(1.0F - THICKNESS, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            default: return VoxelShapes.fullCube();
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch(type) {
            case LAND:
                return true;
            case WATER:
                return world.getFluidState(pos).isIn(FluidTags.WATER);
            case AIR:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) { builder.add(FACING, WATERLOGGED); }

    /*@Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        /*Gravity gravity = ((EntityAccessor)entity).getGravity();
        //NOTICE: Until I can get block to entity collision working, this isn't a bug, its the feature of
        // every gravity panel block creating a 1x1x1 block G-Field towards it. :)
        entity.fallDistance = 0.0F;
        switch (state.get(Properties.FACING)) {
            //Here Directions are inverted because the block is FACING the said direction, not actually going that way,
            // AKA the direction of the top of the block to the bottom is the direction of gravity :)
            case UP:   { gravity.setGravityDirection(entity, 0, false); break; }
            case DOWN: { gravity.setGravityDirection(entity, 1, false); break; }
            case SOUTH:{ gravity.setGravityDirection(entity, 2, false); break; }
            case NORTH:{ gravity.setGravityDirection(entity, 3, false); break; }
            case WEST: { gravity.setGravityDirection(entity, 4, false); break; }
            case EAST: { gravity.setGravityDirection(entity, 5, false); break; }
        }*/
    //}*/
}