package hellozyemlya.factory.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

enum class OpenSide : StringIdentifiable {
    SOUTH,
    WEST,
    EAST,
    NORTH,
    NONE;

    override fun asString(): String {
        return this.name.lowercase()
    }

    public fun opposite(): OpenSide {
        return when (this) {
            EAST -> WEST
            WEST -> EAST
            NORTH -> SOUTH
            SOUTH -> NORTH
            NONE -> NONE
        }
    }

    public fun rotateClockwise(): OpenSide {
        return when (this) {
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
            NORTH -> EAST
            NONE -> NONE
        }
    }
}

enum class VerticalBlockType {
    VERTICAL_INPUT,
    VERTICAL_OUTPUT,
    VERTICAL
}

fun OpenSide.toDirection(): Direction? {
    return when (this) {
        OpenSide.EAST -> Direction.EAST
        OpenSide.NORTH -> Direction.NORTH
        OpenSide.SOUTH -> Direction.SOUTH
        OpenSide.WEST -> Direction.WEST
        else -> null
    }
}

fun Direction.toOpenSide(): OpenSide? {
    return when (this) {
        Direction.EAST -> OpenSide.EAST
        Direction.NORTH -> OpenSide.NORTH
        Direction.SOUTH -> OpenSide.SOUTH
        Direction.WEST -> OpenSide.WEST
        else -> null
    }
}


class ConveyorBeltVerticalBlock(settings: Settings, private val verticalType: VerticalBlockType) : Block(settings) {
    companion object {
        val OpenSideProperty: EnumProperty<OpenSide> = EnumProperty.of("open_side", OpenSide::class.java)

        const val VerticalSpeed = 0.3
        private val EAST_SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
            createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
            createCuboidShape(0.0, 0.0, 1.0, 1.0, 16.0, 15.0)
        ).simplify()
        private val WEST_SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
            createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
            createCuboidShape(15.0, 0.0, 1.0, 16.0, 16.0, 15.0)
        ).simplify()
        private val NORTH_SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
            createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            createCuboidShape(1.0, 0.0, 15.0, 15.0, 16.0, 16.0)
        ).simplify()
        private val SOUTH_SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
            createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            createCuboidShape(1.0, 0.0, 0.0, 15.0, 16.0, 1.0)
        ).simplify()
        private val NONE_SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
            createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
            createCuboidShape(0.0, 0.0, 1.0, 1.0, 16.0, 15.0),
            createCuboidShape(15.0, 0.0, 1.0, 16.0, 16.0, 15.0)
        ).simplify()
    }

    init {
        defaultState = defaultState
            .with(OpenSideProperty, if (verticalType == VerticalBlockType.VERTICAL) OpenSide.NONE else OpenSide.WEST)
    }

    override fun canMobSpawnInside(state: BlockState?): Boolean {
        return true
    }

    private fun calcVerticalVelocityDiff(entity: Entity): Double {
        return if (entity.velocity.y < VerticalSpeed) {
            VerticalSpeed - entity.velocity.y
        } else {
            0.0
        }
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        if (!entity.isSneaking) {
            val yVelocityDiff = calcVerticalVelocityDiff(entity)

            if (verticalType != VerticalBlockType.VERTICAL) {
                val direction = if (verticalType == VerticalBlockType.VERTICAL_INPUT) {
                    state.get(OpenSideProperty).opposite().toDirection()!!
                } else {
                    state.get(OpenSideProperty).toDirection()!!
                }

                entity.velocity = entity.velocity.add(
                    0.06 * (direction.offsetX * 1.5),
                    yVelocityDiff,
                    0.06 * (direction.offsetZ * 1.5)
                )
            } else {
                entity.velocity = entity.velocity.add(0.0, yVelocityDiff, 0.0)
            }
        }
    }


    override fun getOutlineShape(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return when (state.get(OpenSideProperty)!!) {
            OpenSide.NONE -> NONE_SHAPE
            OpenSide.EAST -> EAST_SHAPE
            OpenSide.NORTH -> NORTH_SHAPE
            OpenSide.WEST -> WEST_SHAPE
            OpenSide.SOUTH -> SOUTH_SHAPE
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(OpenSideProperty)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val placedState = super.getPlacementState(ctx)

        if (placedState != null) {
            if (verticalType != VerticalBlockType.VERTICAL) {
                for (facing in ctx.placementDirections) {
                    if (facing.axis.isHorizontal) {
                        return placedState.with(OpenSideProperty, facing.toOpenSide()!!.opposite())
                    }
                }
            } else {
                return placedState
            }
        }

        return null
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (verticalType != VerticalBlockType.VERTICAL) {
            if (player.isSneaking) {
                world.setBlockState(pos, state.with(OpenSideProperty, state.get(OpenSideProperty).rotateClockwise()))
                return ActionResult.SUCCESS
            }
        }

        return super.onUse(state, world, pos, player, hand, hit)
    }
}