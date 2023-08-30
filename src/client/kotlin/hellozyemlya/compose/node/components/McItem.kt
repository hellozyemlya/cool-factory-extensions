package hellozyemlya.compose.node.components

import androidx.compose.runtime.Composable
import hellozyemlya.compose.node.Flex
import hellozyemlya.compose.node.Node
import hellozyemlya.compose.node.height
import hellozyemlya.compose.node.width
import hellozyemlya.compose.rendering.debugBorder
import hellozyemlya.compose.rendering.withMatrices
import net.minecraft.item.ItemStack
import net.minecraft.util.math.ColorHelper

/**
 * Minecraft rendering system renders item with 16px size.
 * All calculations for matrix scale must be performed with this value.
 */
private const val defaultItemHeight = 16

@Composable
fun McItem(stack: ItemStack, size: Int = defaultItemHeight) {
    Node(
        renderer = {
            debugBorder(it) {
                withMatrices { matrices ->
                    val scaleFactor = size.toFloat() / defaultItemHeight
                    matrices.translate(it.layoutNode.layoutX, it.layoutNode.layoutY, 0f)
                    matrices.scale(scaleFactor, scaleFactor, 1f)
                    this.drawItem(stack, 0, 0)
                }
            }
        },
        flex = Flex.width(size.toFloat()).height(size.toFloat())
    )
}