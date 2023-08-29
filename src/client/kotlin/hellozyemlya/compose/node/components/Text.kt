package hellozyemlya.compose.node.components

import androidx.compose.runtime.Composable
import com.facebook.yoga.YogaMeasureMode
import hellozyemlya.compose.node.InterfaceNode
import hellozyemlya.compose.node.Node
import net.minecraft.client.MinecraftClient

@Composable
fun Text(text: String) {
    Node(
        renderer = {
            this.drawText(MinecraftClient.getInstance().textRenderer, text, it.layoutNode.layoutX.toInt(), it.layoutNode.layoutY.toInt(), 0x00FF00, false)
        },
        measureFunction = {
                node: InterfaceNode, width: Float,
                widthMode: YogaMeasureMode,
                height: Float,
                heightMode: YogaMeasureMode ->
            MinecraftClient.getInstance().textRenderer.getWidth(text).toFloat() to 9f
        }
    )
}
