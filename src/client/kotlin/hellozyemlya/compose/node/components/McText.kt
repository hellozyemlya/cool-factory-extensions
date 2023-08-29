package hellozyemlya.compose.node.components

import androidx.compose.runtime.Composable
import com.facebook.yoga.YogaMeasureMode
import hellozyemlya.compose.node.*
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.ColorHelper

@Composable
fun McText(text: String) {
    Node(
        renderer = {
            this.drawBorder(it.layoutNode.layoutX.toInt(),
                it.layoutNode.layoutY.toInt(),
                it.layoutNode.layoutWidth.toInt(),
                it.layoutNode.layoutHeight.toInt(),
                ColorHelper.Argb.getArgb(255, 255, 0, 0))
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
