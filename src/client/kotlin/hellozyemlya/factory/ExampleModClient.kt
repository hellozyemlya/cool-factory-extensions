package hellozyemlya.factory

import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.systems.VertexSorter
import hellozyemlya.compose.ClientTickDispatcher
import hellozyemlya.compose.ImageFrameBuffer
import hellozyemlya.compose.MinecraftSkiaDrawUtils
import hellozyemlya.compose.SkiaImageImageBitmap
import net.fabricmc.api.ClientModInitializer
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
import org.jetbrains.skia.IRect
import org.jetbrains.skia.Image
import org.joml.Matrix4f


object ExampleModClient : ClientModInitializer {
    val itemToIcon = mutableMapOf<Item, SkiaImageImageBitmap>()

    val mainScene by lazy {
        ComposeScene(ClientTickDispatcher, density = Density(2f)).also { it.setContent { App() } }
    }

    val iconsAtlas by lazy {
        MinecraftSkiaDrawUtils.INSTANCE.gpuImage(4096, 4096)
    }

    val iconsAtlasFbo by lazy {
        val res = ImageFrameBuffer(iconsAtlas)
        println("image fbo created...")
        res
    }

    val rawFbo by lazy  {
        SimpleFramebuffer(4096, 4096, true, MinecraftClient.IS_SYSTEM_MAC)
    }
    override fun onInitializeClient() {
        ClientTickDispatcher.setup()

        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            println("generating images...")
            val fbo =
            MinecraftSkiaDrawUtils.INSTANCE.renderToFbo(iconsAtlasFbo) { ctx ->
                // default item is 16, I want them to be 64 pixels
                ctx.matrices.scale(4f, 4f, 1f)
                Registries.ITEM.forEachIndexed { index, item ->
                    val row = index / 64
                    val col = index % 64
                    val x = (col * 64) / 4
                    val y = (row * 64)  / 4
                    ctx.drawItem(item.defaultStack, x, y)
                }
            }
//            val _atlasImage = fbo.toImage()
            println("Rendered atlas")
//            Registries.ITEM.forEachIndexed { index, item ->
//                val row = index / 64
//                val col = index % 64
//                val x = (col * 64) / 4
//                val y = (row * 64)  / 4
//                val subset = iconsAtlas.makeSubset(IRect.makeXYWH(x, y, 64, 64), MinecraftSkiaDrawUtils.INSTANCE.context)
////                println("Generated ${index}")
//                itemToIcon[item] = SkiaImageImageBitmap(subset)
//            }
            println("image generation finished")
        }
        HudRenderCallback.EVENT.register { ctx, delta ->
            MinecraftSkiaDrawUtils.INSTANCE.render { canvas, surface ->
                mainScene.constraints = Constraints(maxWidth = surface.width, maxHeight = surface.height)
                mainScene.render(canvas, System.nanoTime())
                canvas.drawImage(iconsAtlas, 0f, 0f)
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