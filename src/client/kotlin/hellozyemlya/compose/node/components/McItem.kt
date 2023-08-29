package hellozyemlya.compose.node.components

import androidx.compose.runtime.Composable
import hellozyemlya.compose.node.Flex
import hellozyemlya.compose.node.Node
import hellozyemlya.compose.node.height
import hellozyemlya.compose.node.width
import net.minecraft.item.ItemStack
import net.minecraft.util.math.ColorHelper

@Composable
fun McItem(stack: ItemStack, size: Int = 16) {
    Node(
        renderer = {
            this.drawBorder(
                it.layoutNode.layoutX.toInt(),
                it.layoutNode.layoutY.toInt(),
                it.layoutNode.layoutWidth.toInt(),
                it.layoutNode.layoutHeight.toInt(),
                ColorHelper.Argb.getArgb(255, 255, 0, 0)
            )
            this.matrices.push()
            val scaleFactor = size.toFloat() / 16
            this.matrices.translate(it.layoutNode.layoutX, it.layoutNode.layoutY, 0f)
            this.matrices.scale(scaleFactor, scaleFactor, 1f)
            this.drawItem(stack, 0, 0)
            this.matrices.pop()
        },
        flex = Flex.width(size.toFloat()).height(size.toFloat())
    )
}