package hellozyemlya.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import org.jetbrains.skia.*

private fun org.jetbrains.skia.ColorSpace?.toComposeColorSpace(): ColorSpace {
    return when (this) {
        org.jetbrains.skia.ColorSpace.sRGB -> ColorSpaces.Srgb
        org.jetbrains.skia.ColorSpace.sRGBLinear -> ColorSpaces.LinearSrgb
        org.jetbrains.skia.ColorSpace.displayP3 -> ColorSpaces.DisplayP3
        else -> ColorSpaces.Srgb
    }
}

private fun ColorType.toComposeConfig() = when (this) {
    ColorType.N32 -> ImageBitmapConfig.Argb8888
    ColorType.ALPHA_8 -> ImageBitmapConfig.Alpha8
    ColorType.RGB_565 -> ImageBitmapConfig.Rgb565
    ColorType.RGBA_F16 -> ImageBitmapConfig.F16
    else -> ImageBitmapConfig.Argb8888
}

private fun FilterQuality.toSkia(): SamplingMode = when (this) {
    FilterQuality.Low -> FilterMipmap(FilterMode.LINEAR, MipmapMode.NONE)
    FilterQuality.Medium -> FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST)
    FilterQuality.High -> CubicResampler(1 / 3.0f, 1 / 3.0f)
    else -> FilterMipmap(FilterMode.NEAREST, MipmapMode.NONE)
}

inline fun unpackFloat1(value: Long): Float {
    return Float.fromBits(value.shr(32).toInt())
}

inline fun unpackFloat2(value: Long): Float {
    return Float.fromBits(value.and(0xFFFFFFFF).toInt())
}

/**
 * ImageBitmap implementation that using direct canvas.drawImage(Image, ...) method call,
 * without any intermediate Bitmap instance creation. This opens ability to use texture-backed
 * Image instances, for performance and RTT scenarios.
 */
class SkiaImageImageBitmap(private val image: Image, private val sourceRect: Rect) : ImageBitmap {
    override val colorSpace = image.colorSpace.toComposeColorSpace()
    override val config = image.colorType.toComposeConfig()
    override val hasAlpha = !image.isOpaque
    override val height get() = sourceRect.height.toInt()
    override val width get() = sourceRect.width.toInt()
    override fun prepareToDraw() = Unit
    override fun readPixels(
        buffer: IntArray,
        startX: Int,
        startY: Int,
        width: Int,
        height: Int,
        bufferOffset: Int,
        stride: Int
    ) {
        TODO("Not yet implemented")
    }

    public fun doDraw(
        canvas: Canvas,
        srcOffset: Long,
        srcSize: Long,
        dstOffset: Long,
        dstSize: Long,
        composePaint: Paint,
        skiaPaint: org.jetbrains.skia.Paint
    ) {

        canvas.drawImageRect(
            image,
            sourceRect,
            Rect.makeXYWH(
                unpackFloat1(dstOffset),
                unpackFloat2(dstOffset),
                unpackFloat1(dstSize),
                unpackFloat2(dstSize)
            ),
            composePaint.filterQuality.toSkia(),
            skiaPaint,
            true
        )
    }
}