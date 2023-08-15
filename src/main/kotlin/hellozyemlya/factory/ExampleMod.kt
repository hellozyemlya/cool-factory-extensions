package hellozyemlya.factory

import hellozyemlya.factory.blocks.ConveyorBeltBlock
import hellozyemlya.factory.blocks.ConveyorBeltVerticalBlock
import hellozyemlya.factory.blocks.VerticalBlockType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.*
import org.slf4j.LoggerFactory


object ExampleMod : ModInitializer {
    private val logger = LoggerFactory.getLogger("cool-factory-extensions")
    private val ConveyorBeltBlock = ConveyorBeltBlock(FabricBlockSettings.create().strength(4.0f).collidable(false))
    private val ConveyorBeltVerticalBlockSettings =
        FabricBlockSettings.create().strength(4.0f).collidable(false).nonOpaque()
    private val CONVEYOR_BELT_VERTICAL_BLOCK: ConveyorBeltVerticalBlock =
        ConveyorBeltVerticalBlock(ConveyorBeltVerticalBlockSettings, VerticalBlockType.VERTICAL)
    private val CONVEYOR_BELT_VERTICAL_INPUT_BLOCK =
        ConveyorBeltVerticalBlock(ConveyorBeltVerticalBlockSettings, VerticalBlockType.VERTICAL_INPUT)
    private val CONVEYOR_BELT_VERTICAL_OUTPUT_BLOCK =
        ConveyorBeltVerticalBlock(ConveyorBeltVerticalBlockSettings, VerticalBlockType.VERTICAL_OUTPUT)

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

        Registry.register(
            Registries.BLOCK,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical"),
            CONVEYOR_BELT_VERTICAL_BLOCK
        )

        Registry.register(
            Registries.ITEM,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical"),
            BlockItem(CONVEYOR_BELT_VERTICAL_BLOCK, FabricItemSettings())
        )

        Registry.register(
            Registries.BLOCK,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical_input"),
            CONVEYOR_BELT_VERTICAL_INPUT_BLOCK
        )

        Registry.register(
            Registries.ITEM,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical_input"),
            BlockItem(CONVEYOR_BELT_VERTICAL_INPUT_BLOCK, FabricItemSettings())
        )

        Registry.register(
            Registries.BLOCK,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical_output"),
            CONVEYOR_BELT_VERTICAL_OUTPUT_BLOCK
        )

        Registry.register(
            Registries.ITEM,
            Identifier("cool-factory-extensions", "conveyor_belt_vertical_output"),
            BlockItem(CONVEYOR_BELT_VERTICAL_OUTPUT_BLOCK, FabricItemSettings())
        )

        logger.info("Hello Fabric world!")
    }
}