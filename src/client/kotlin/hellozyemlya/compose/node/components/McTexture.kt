package hellozyemlya.compose.node.components

import androidx.compose.runtime.Composable
import hellozyemlya.compose.node.Flex
import hellozyemlya.compose.node.Node
import hellozyemlya.compose.node.height
import hellozyemlya.compose.node.width
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.util.Identifier

@Composable
fun McTexture() {
    Node(
        renderer = {
            drawNineSlicedTexture(
                ClickableWidget.WIDGETS_TEXTURE,
                it.layoutNode.layoutX.toInt(),
                it.layoutNode.layoutY.toInt(),
                200,
                200,
                20,
                4,
                200,
                20,
                0,
                46 + 1 * 20
            )
        },
        flex = Flex.width(200f).height(200f)
    )
}