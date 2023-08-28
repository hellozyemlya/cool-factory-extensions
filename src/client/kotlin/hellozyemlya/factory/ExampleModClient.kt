package hellozyemlya.factory

import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.systems.VertexSorter
import hellozyemlya.compose.*
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


    override fun onInitializeClient() {
        ClientTickDispatcher.setup()

        HudRenderCallback.EVENT.register { ctx, delta ->

        }
    }
}

