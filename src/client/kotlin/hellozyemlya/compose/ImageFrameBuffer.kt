package hellozyemlya.compose

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import org.jetbrains.skia.BackendApi
import org.jetbrains.skia.Image
import java.nio.IntBuffer

class ImageFrameBuffer(private val image: Image) : Framebuffer(true) {
    init {
        RenderSystem.assertOnRenderThreadOrInit()
        val backendTexture = image.getBackendTexture(false) ?: throw IllegalArgumentException("Image is not texture-backed")
        if(!(backendTexture.isValid && backendTexture.backend == BackendApi.OPENGL)) {
            throw IllegalArgumentException("Image is not opengl backed")
        }
        this.resize(image.width, image.height, MinecraftClient.IS_SYSTEM_MAC)
    }

    override fun initFbo(width: Int, height: Int, getError: Boolean) {
        RenderSystem.assertOnRenderThreadOrInit()
        val i = RenderSystem.maxSupportedTextureSize()
        colorAttachment = image.getBackendTexture()!!.glTextureId
        if (width > 0 && width <= i && height > 0 && height <= i) {
            viewportWidth = width
            viewportHeight = height
            textureWidth = width
            textureHeight = height
            fbo = GlStateManager.glGenFramebuffers()
            if (useDepthAttachment) {
                depthAttachment = TextureUtil.generateTextureId()
                GlStateManager._bindTexture(depthAttachment)
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST)
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST)
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_COMPARE_MODE, 0)
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_S, GlConst.GL_CLAMP_TO_EDGE)
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_T, GlConst.GL_CLAMP_TO_EDGE)
                GlStateManager._texImage2D(
                    GlConst.GL_TEXTURE_2D, 0, GlConst.GL_DEPTH_COMPONENT,
                    textureWidth, textureHeight, 0, GlConst.GL_DEPTH_COMPONENT, GlConst.GL_FLOAT, null as IntBuffer?
                )
            }
            setTexFilter(GlConst.GL_NEAREST)
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, fbo)
            println("--- binding texture - ${image.getBackendTexture()!!.glTextureId}")
            GlStateManager._glFramebufferTexture2D(
                GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D,
                image.getBackendTexture()!!.glTextureId, 0
            )
            if (useDepthAttachment) {
                GlStateManager._glFramebufferTexture2D(
                    GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D,
                    depthAttachment, 0
                )
            }
            checkFramebufferStatus()
            this.clear(getError)
            endRead()
        } else {
            throw java.lang.IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")")
        }
    }
}