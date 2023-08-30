package hellozyemlya.compose.node.components

import androidx.compose.runtime.Composable
import com.facebook.yoga.YogaMeasureMode
import hellozyemlya.compose.node.*
import hellozyemlya.compose.rendering.debugBorder
import hellozyemlya.compose.rendering.drawText
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.ColorHelper

@Composable
fun McText(text: String) {
    Node(
        renderer = {
            debugBorder(it) {
                this.drawText(text, it.layoutNode.layoutX, it.layoutNode.layoutY, 0x00FF00, false)
            }
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
