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
    SOUTH {
        override fun asString(): String {
            return "south"
        }
    },
    WEST {
        override fun asString(): String {
            return "west"
        }
    },
    EAST {
        override fun asString(): String {
            return "east"
        }
    },
    NORTH {
        override fun asString(): String {
            return "north"
        }
    },
    NONE {
        override fun asString(): String {
            return "none"
        }
    }
}

enum class VerticalBlockType : StringIdentifiable {
    VERTICAL_INPUT {
        override fun asString(): String {
            return "input"
        }
    },
    VERTICAL_OUTPUT {
        override fun asString(): String {
            return "output"
        }
    },
    VERTICAL {
        override fun asString(): String {
            return "none"
        }
    }
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

val OpenSideProperty: EnumProperty<OpenSide> = EnumProperty.of("open_side", OpenSide::class.java)

const val VerticalSpeed = 0.3

class ConveyorBeltVerticalBlock(settings: Settings, private val verticalType: VerticalBlockType) : Block(settings) {
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
            val direction = state.get(OpenSideProperty).toDirection()

            val yVelocityDiff = calcVerticalVelocityDiff(entity)

            if (direction != null) {
                when (verticalType) {
                    VerticalBlockType.VERTICAL_INPUT -> {
                        entity.velocity = entity.velocity.add(
                            0.06 * (direction.opposite.offsetX * 1.5),
                            yVelocityDiff,
                            0.06 * (direction.opposite.offsetZ * 1.5)
                        )
                    }

                    VerticalBlockType.VERTICAL_OUTPUT -> {
                        entity.velocity = entity.velocity.add(
                            0.06 * (direction.offsetX * 1.5),
                            yVelocityDiff,
                            0.06 * (direction.offsetZ * 1.5)
                        )
                    }

                    VerticalBlockType.VERTICAL -> {
                        entity.velocity = entity.velocity.add(
                            0.0,
                            yVelocityDiff,
                            0.0
                        )
                    }
                }
            } else {
                entity.velocity = entity.velocity.add(0.0, yVelocityDiff, 0.0)
            }
            println(entity.velocity.y)
        }
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        val bot = createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
        val top = createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0)
        val left = createCuboidShape(0.0, 1.0, 0.0, 1.0, 16.0, 15.0)
        val right = createCuboidShape(15.0, 1.0, 0.0, 16.0, 16.0, 15.0)

        return VoxelShapes.union(bot, top, left, right).simplify()
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
                        return placedState.with(OpenSideProperty, facing.toOpenSide()!!)
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
        if(verticalType != VerticalBlockType.VERTICAL) {
            if (player.isSneaking) {
                val direction = state.get(OpenSideProperty).toDirection()!!
                val newOpenSide = direction.rotateClockwise(Direction.Axis.Y).toOpenSide()!!
                world.setBlockState(pos, state.with(OpenSideProperty, newOpenSide))
                return ActionResult.SUCCESS
            }
        }

        return super.onUse(state, world, pos, player, hand, hit)
    }
}