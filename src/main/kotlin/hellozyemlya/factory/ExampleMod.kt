package hellozyemlya.factory

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import org.slf4j.LoggerFactory

class ConveyorBeltBlock(settings: Settings) : Block(settings) {
    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun canMobSpawnInside(state: BlockState?): Boolean {
        return true
    }

    override fun onEntityCollision(state: BlockState?, world: World?, pos: BlockPos?, entity: Entity?) {
        if (entity != null && state != null) {
            if (!entity.isCrawling) {
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
val OpenSideProperty = EnumProperty.of("open_side", OpenSide::class.java)

class ConveyorBeltVerticalBlock(settings: Settings) : Block(settings) {
    init {
        defaultState = defaultState.with(OpenSideProperty, OpenSide.NONE)
    }
    override fun canMobSpawnInside(state: BlockState?): Boolean {
        return true
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        if (!entity.isCrawling) {
            val direction = when(state.get(OpenSideProperty)) {
                OpenSide.EAST -> Direction.EAST
                OpenSide.NORTH -> Direction.NORTH
                OpenSide.SOUTH -> Direction.SOUTH
                OpenSide.WEST -> Direction.WEST
                else -> null
            }

            if(direction != null) {
                val down1 = world.getBlockState(pos.down()).block
                val down2 = world.getBlockState(pos.down().down()).block
                if(down1 == ExampleMod.ConveyorBeltVerticalBlock && down2 != ExampleMod.ConveyorBeltVerticalBlock || down1 != ExampleMod.ConveyorBeltVerticalBlock ) {
                    entity.velocity =  entity.velocity.add(0.06 * (direction.opposite.offsetX * 1.5), 0.06, 0.06 * (direction.opposite.offsetZ * 1.5))
                } else {
                    entity.velocity =  entity.velocity.add(0.06 * (direction.offsetX * 1.5), 0.06, 0.06 * (direction.offsetZ * 1.5))
                }
            } else {
                entity.velocity = entity.velocity.add(0.0, 0.06, 0.0)
            }
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
            return if(ctx.world.getBlockState(ctx.blockPos.north()).block == Blocks.AIR) {
                placedState.with(OpenSideProperty, OpenSide.NORTH)
            } else if(ctx.world.getBlockState(ctx.blockPos.east()).block == Blocks.AIR) {
                placedState.with(OpenSideProperty, OpenSide.EAST)
            } else if(ctx.world.getBlockState(ctx.blockPos.west()).block == Blocks.AIR) {
                placedState.with(OpenSideProperty, OpenSide.WEST)
            } else if(ctx.world.getBlockState(ctx.blockPos.south()).block == Blocks.AIR) {
                placedState.with(OpenSideProperty, OpenSide.SOUTH)
            } else {
                placedState
            }
        }

        return placedState
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState?,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if(world.getBlockState(pos.north()).block == Blocks.AIR) {
            state.with(OpenSideProperty, OpenSide.NORTH)
        } else if(world.getBlockState(pos.east()).block == Blocks.AIR) {
            state.with(OpenSideProperty, OpenSide.EAST)
        } else if(world.getBlockState(pos.west()).block == Blocks.AIR) {
            state.with(OpenSideProperty, OpenSide.WEST)
        } else if(world.getBlockState(pos.south()).block == Blocks.AIR) {
            state.with(OpenSideProperty, OpenSide.SOUTH)
        } else {
            state
        }
    }
}
object ExampleMod : ModInitializer {
    private val logger = LoggerFactory.getLogger("cool-factory-extensions")
    private val ConveyorBeltBlock = ConveyorBeltBlock(FabricBlockSettings.create().strength(4.0f).collidable(false))
    public val ConveyorBeltVerticalBlock = ConveyorBeltVerticalBlock(FabricBlockSettings.create().strength(4.0f).collidable(false).nonOpaque())

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        Registry.register(Registries.BLOCK, Identifier("cool-factory-extensions", "conveyor_belt"), ConveyorBeltBlock)
        Registry.register(
            Registries.ITEM,
            Identifier("cool-factory-extensions", "conveyor_belt"),
            BlockItem(ConveyorBeltBlock, FabricItemSettings())
        )

        Registry.register(Registries.BLOCK, Identifier("cool-factory-extensions", "conveyor_belt_vertical"), ConveyorBeltVerticalBlock)
        Registry.register(
            Registries.ITEM,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical"),
            BlockItem(ConveyorBeltVerticalBlock, FabricItemSettings())
        )

        logger.info("Hello Fabric world!")
    }
}