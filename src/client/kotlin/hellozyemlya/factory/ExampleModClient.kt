package hellozyemlya.factory

import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.mojang.blaze3d.platform.GlConst.*
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.systems.VertexSorter
import hellozyemlya.compose.ClientTickDispatcher
import hellozyemlya.compose.MinecraftSurfaceManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.item.Items
import org.joml.Matrix4f
import java.io.File


object ExampleModClient : ClientModInitializer {
    val mainScene by lazy {
        ComposeScene(ClientTickDispatcher, density = Density(2f)).also { it.setContent { App() } }
    }

    var rendered = false
    override fun onInitializeClient() {
        ClientTickDispatcher.setup()

        HudRenderCallback.EVENT.register { ctx, delta ->
//            MinecraftSurfaceManager.INSTANCE.render { canvas, surface ->
//                mainScene.constraints = Constraints(maxWidth = surface.width, maxHeight = surface.height)
//                mainScene.render(canvas, System.nanoTime())
//            }
            if(!rendered) {
                rendered = true
                val dimensions = 64
                val fbo = SimpleFramebuffer(dimensions, dimensions, true, MinecraftClient.IS_SYSTEM_MAC)
                fbo.setClearColor(0f, 1f, 0f, 0f)
                fbo.clear(MinecraftClient.IS_SYSTEM_MAC)
                fbo.beginWrite(true)
                val matrix4f = Matrix4f().setOrtho(
                    0.0f,
                    dimensions.toFloat(),
                    dimensions.toFloat(),
                    0.0f,
                    1000.0f,
                    21000.0f
                )
                RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z)
                val matrixStack = RenderSystem.getModelViewStack()
                matrixStack.push()
                matrixStack.loadIdentity()
                matrixStack.translate(0.0f, 0.0f, -11000.0f)
                matrixStack.scale((dimensions / 16).toFloat(), (dimensions / 16).toFloat(), 1f)
                RenderSystem.applyModelViewMatrix()
                DiffuseLighting.enableGuiDepthLighting()
                val drawContext = DrawContext(MinecraftClient.getInstance(), MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers)
                drawContext.drawItem(Items.DIAMOND.defaultStack, 0, 0)
                drawContext.draw()
                fbo.endWrite()
                fbo.doScreenshot("hello.png")
                fbo.delete()
                rendered = true
            }
        }
    }
}

fun Framebuffer.doScreenshot(filename: String) {
    val i: Int = this.textureWidth
    val j: Int = this.textureHeight
    val nativeImage = NativeImage(i, j, false)
    RenderSystem.bindTexture(this.colorAttachment)
    nativeImage.loadFromTextureImage(0, false)
    nativeImage.mirrorVertically()
    nativeImage.writeTo(File(filename))
    nativeImage.close()
}