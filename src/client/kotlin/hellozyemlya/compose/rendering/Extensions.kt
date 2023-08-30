package hellozyemlya.compose.rendering

import hellozyemlya.compose.node.InterfaceNode
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.ColorHelper

fun DrawContext.drawItem(item: ItemStack, x: Float, y: Float) {
    this.drawItem(item, x.toInt(), y.toInt())
}

fun DrawContext.drawText(text: String, x: Float, y: Float, color: Int, shadow: Boolean) {
    this.drawText(MinecraftClient.getInstance().textRenderer, text, x.toInt(), y.toInt(), color, shadow)
}

fun DrawContext.drawBorder(x: Float, y: Float, width: Float, height: Float, color: Int) {
    this.drawBorder(x.toInt(), y.toInt(), width.toInt(), height.toInt(), color)
}

fun DrawContext.debugBorder(node: InterfaceNode, block: DrawContext.() -> Unit) {
    this.drawBorder(node.layoutNode.layoutX,
        node.layoutNode.layoutY,
        node.layoutNode.layoutWidth,
        node.layoutNode.layoutHeight,
        ColorHelper.Argb.getArgb(255, 255, 0, 0))
    block()
}

fun DrawContext.withMatrices(block: DrawContext.(matrices: MatrixStack) -> Unit) {
    this.matrices.push()
    block(this.matrices)
    this.matrices.pop()
}