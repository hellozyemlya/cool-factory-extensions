package hellozyemlya.compose

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.systems.VertexSorter
import hellozyemlya.factory.ExampleModClient
import hellozyemlya.factory.toImage
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BackgroundRenderer
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.registry.Registries
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import org.joml.Matrix4f

interface SkiaDrawUtils {
    val context: DirectContext
    val surface: Surface
    val canvas: Canvas
    fun render(block: (canvas: Canvas, surface: Surface) -> Unit)
    fun gpuImage(width: Int, height: Int): Image
    fun renderToFbo(fbo: Framebuffer, block: (DrawContext) -> Unit) {
        fbo.setClearColor(1f, 1f, 1f, 0f)
        fbo.clear(MinecraftClient.IS_SYSTEM_MAC)
        RenderSystem.clear(
            GlConst.GL_DEPTH_BUFFER_BIT or GlConst.GL_COLOR_BUFFER_BIT,
            MinecraftClient.IS_SYSTEM_MAC
        )

        fbo.beginWrite(true)
        BackgroundRenderer.clearFog()
        RenderSystem.enableCull()
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC)
        val matrix4f = Matrix4f().setOrtho(
            0.0f,
            fbo.textureWidth.toFloat(),
            fbo.textureHeight.toFloat(),
            0.0f,
            1000.0f,
            21000.0f
        )
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z)
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.loadIdentity()
        matrixStack.translate(0.0f, 0.0f, -11000.0f)
        RenderSystem.applyModelViewMatrix()
        DiffuseLighting.enableGuiDepthLighting()

        val drawContext = DrawContext(
            MinecraftClient.getInstance(),
            MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        )

        block(drawContext)

        drawContext.draw()
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC)
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
        fbo.endWrite()
    }
}