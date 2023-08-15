package hellozyemlya.factory.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class ConveyorBeltBlock(settings: Settings) : Block(settings) {
    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun canMobSpawnInside(state: BlockState?): Boolean {
        return true
    }

    override fun onEntityCollision(state: BlockState?, world: World?, pos: BlockPos?, entity: Entity?) {
        if (entity != null && state != null) {
            if (!entity.isSneaking) {
                val direction: Direction = state.get(Properties.HORIZONTAL_FACING)
                entity.velocity =
                    entity.velocity.add(0.06 * (direction.offsetX * 1.5), 0.0, 0.06 * (direction.offsetZ * 1.5))
            }
        }
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult
    ): ActionResult {
        if (player.isSneaking) {
            world.setBlockState(pos, rotate(state, BlockRotation.CLOCKWISE_90))
            return ActionResult.SUCCESS
        }

        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(
            Properties.HORIZONTAL_FACING,
            rotation.rotate(state.get(Properties.HORIZONTAL_FACING))
        )
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.with(
            Properties.HORIZONTAL_FACING,
            mirror.apply(state.get(Properties.HORIZONTAL_FACING))
        )
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val placedState = super.getPlacementState(ctx)

        if (placedState != null) {
            for (facing in ctx.placementDirections) {
                if (facing.axis.isHorizontal) {
                    return placedState.with(Properties.HORIZONTAL_FACING, facing)
                }
            }
        }

        return placedState
    }
}