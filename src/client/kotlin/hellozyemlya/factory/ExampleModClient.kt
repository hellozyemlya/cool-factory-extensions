package hellozyemlya.factory

import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import com.mojang.blaze3d.systems.RenderSystem
import hellozyemlya.compose.ClientTickDispatcher
import hellozyemlya.compose.MinecraftSurfaceManager
import kotlinx.coroutines.runBlocking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.render.BufferRenderer
import org.jetbrains.skia.*
import org.jetbrains.skia.FramebufferFormat.Companion.GR_GL_RGBA8
import org.jetbrains.skiko.FrameDispatcher
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL33
import kotlin.coroutines.CoroutineContext

object ExampleModClient : ClientModInitializer {
    val mainScene by lazy {
        ComposeScene(ClientTickDispatcher).also { it.setContent { App() } }
    }
    override fun onInitializeClient() {
        ClientTickDispatcher.setup()

        HudRenderCallback.EVENT.register { ctx, delta ->
            MinecraftSurfaceManager.INSTANCE.render { canvas, surface ->
                mainScene.constraints = Constraints(maxWidth = surface.width, maxHeight = surface.height)
                mainScene.render(canvas, System.nanoTime())
            }
        }
    }
}