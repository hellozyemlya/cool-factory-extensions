package hellozyemlya.factory

import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.systems.VertexSorter
import hellozyemlya.compose.ClientTickDispatcher
import hellozyemlya.compose.MinecraftSurfaceManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BackgroundRenderer
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.texture.NativeImage
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import org.jetbrains.skia.Image
import org.joml.Matrix4f


object ExampleModClient : ClientModInitializer {
    val itemToIcon = mutableMapOf<Item, Image>()

    val mainScene by lazy {
        ComposeScene(ClientTickDispatcher, density = Density(2f)).also { it.setContent { App() } }
    }

    override fun onInitializeClient() {
        ClientTickDispatcher.setup()

        ClientLifecycleEvents.CLIENT_STARTED.register {

        }
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            println("generating images...")
            val textureDim = 64
            val fbo = SimpleFramebuffer(textureDim, textureDim, true, MinecraftClient.IS_SYSTEM_MAC)
            fbo.setClearColor(0f, 1f, 0f, 0f)

            Registries.ITEM.forEach {
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
                    textureDim.toFloat(),
                    textureDim.toFloat(),
                    0.0f,
                    1000.0f,
                    21000.0f
                )
                RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z)
                val matrixStack = RenderSystem.getModelViewStack()
                matrixStack.push()
                matrixStack.loadIdentity()
                matrixStack.translate(0.0f, 0.0f, -11000.0f)
                matrixStack.scale((textureDim / 16).toFloat(), (textureDim / 16).toFloat(), 1f)
                RenderSystem.applyModelViewMatrix()
                DiffuseLighting.enableGuiDepthLighting()

                val drawContext = DrawContext(
                    MinecraftClient.getInstance(),
                    MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
                )

                drawContext.drawItem(it.defaultStack, 0, 0)

                drawContext.draw()
                RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC)
                matrixStack.pop()
                RenderSystem.applyModelViewMatrix()
                fbo.endWrite()
                itemToIcon[it] = fbo.toImage()
            }
            fbo.delete()
            println("image generation finished")
        }
        HudRenderCallback.EVENT.register { ctx, delta ->
            MinecraftSurfaceManager.INSTANCE.render { canvas, surface ->
                mainScene.constraints = Constraints(maxWidth = surface.width, maxHeight = surface.height)
                mainScene.render(canvas, System.nanoTime())
            }
        }
    }
}

//fun Framebuffer.doScreenshot(filename: String) {
//    val i: Int = this.textureWidth
//    val j: Int = this.textureHeight
//    val nativeImage = NativeImage(i, j, false)
//    RenderSystem.bindTexture(this.colorAttachment)
//    nativeImage.loadFromTextureImage(0, false)
//    nativeImage.mirrorVertically()
//    nativeImage.writeTo(File(filename))
//    nativeImage.close()
//}

fun Framebuffer.toImage(): Image {
    val i: Int = this.textureWidth
    val j: Int = this.textureHeight
    NativeImage(i, j, false).use { nativeImage ->
        RenderSystem.bindTexture(this.colorAttachment)
        nativeImage.loadFromTextureImage(0, false)
        nativeImage.mirrorVertically()
        return Image.makeFromEncoded(nativeImage.bytes)
    }
}