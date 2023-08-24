package hellozyemlya.compose

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Surface

interface SurfaceManager {
    val surface: Surface
    val canvas: Canvas
    fun render(block: (canvas: Canvas, surface: Surface) -> Unit)
}