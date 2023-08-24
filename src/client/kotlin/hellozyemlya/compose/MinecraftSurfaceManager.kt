package hellozyemlya.compose

import com.mojang.blaze3d.systems.RenderSystem
import kotlinx.coroutines.runBlocking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.render.BufferRenderer
import org.jetbrains.skia.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33

class MinecraftSurfaceManager : SurfaceManager {
    companion object {
        val INSTANCE by lazy {
            MinecraftSurfaceManager()
        }
    }

    private val directSkiaContext by lazy {
        DirectContext.makeGL()
    }
    private lateinit var _surface: Surface
    private lateinit var _canvas: Canvas
    private lateinit var backendRenderTarget: BackendRenderTarget

    init {
        resize(MinecraftClient.getInstance().framebuffer)
    }

    override val surface: Surface
        get() = _surface

    override val canvas: Canvas
        get() = _canvas

    fun resize(framebuffer: Framebuffer) {
        cleanup()
        backendRenderTarget = BackendRenderTarget.makeGL(
            framebuffer.textureWidth,
            framebuffer.textureHeight,
            0,
            8,
            framebuffer.fbo,
            FramebufferFormat.GR_GL_RGBA8
        )
        _surface = Surface.makeFromBackendRenderTarget(
            directSkiaContext, backendRenderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888,
            ColorSpace.displayP3,
            SurfaceProps(PixelGeometry.RGB_H)
        )!!
        _canvas = _surface.canvas
        println("Resized to ${framebuffer.textureWidth}, ${framebuffer.textureHeight}")
    }

    private fun cleanup() {
        if (this::_surface.isInitialized) {
            this._surface.close()
        }
        if (this::backendRenderTarget.isInitialized) {
            this.backendRenderTarget.close()
        }
    }

    override fun render(block: (canvas: Canvas, surface: Surface) -> Unit) {
        beforeRender()
        block(canvas, surface)
        afterRender()
    }

    private fun beforeRender() {
        RenderSystem.assertOnRenderThread()

        RenderSystem.pixelStore(GL30.GL_UNPACK_ROW_LENGTH, 0)
        RenderSystem.pixelStore(GL30.GL_UNPACK_SKIP_PIXELS, 0)
        RenderSystem.pixelStore(GL30.GL_UNPACK_SKIP_ROWS, 0)
        RenderSystem.pixelStore(GL30.GL_UNPACK_ALIGNMENT, 4)
        directSkiaContext.resetAll()
    }

    private fun afterRender() {
        RenderSystem.assertOnRenderThread()
        surface.flush()
        BufferRenderer.reset()
        GL33.glBindSampler(0, 0)
        RenderSystem.disableBlend()
        GL30.glDisable(GL30.GL_BLEND)
        RenderSystem.blendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE)
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE)
        RenderSystem.blendEquation(GL30.GL_FUNC_ADD)
        GL30.glBlendEquation(GL30.GL_FUNC_ADD)
        RenderSystem.colorMask(true, true, true, true)
        GL30.glColorMask(true, true, true, true)
        RenderSystem.depthMask(true)
        GL30.glDepthMask(true)
        RenderSystem.disableScissor()
        GL30.glDisable(GL30.GL_SCISSOR_TEST)
        GL30.glDisable(GL30.GL_STENCIL_TEST)
        RenderSystem.disableDepthTest()
        GL30.glDisable(GL30.GL_DEPTH_TEST)
        GL30.glActiveTexture(GL30.GL_TEXTURE0)
        RenderSystem.activeTexture(GL30.GL_TEXTURE0)
    }
}