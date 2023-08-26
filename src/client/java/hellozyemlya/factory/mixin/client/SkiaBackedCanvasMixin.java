package hellozyemlya.factory.mixin.client;

import androidx.compose.ui.graphics.ImageBitmap;
import androidx.compose.ui.graphics.Paint;
import hellozyemlya.compose.SkiaImageImageBitmap;
import org.jetbrains.skia.Canvas;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"androidx.compose.ui.graphics.SkiaBackedCanvas"}, remap = false)
public abstract class SkiaBackedCanvasMixin {
    @Final
    @Shadow
    Canvas skia;

    @Shadow
    abstract org.jetbrains.skia.Paint getSkia(androidx.compose.ui.graphics.Paint paint);

    @Inject(method = "*(Landroidx/compose/ui/graphics/ImageBitmap;JJJJLandroidx/compose/ui/graphics/Paint;)V", at = @At("HEAD"), cancellable = true)
    private void drawImageRectForImage(ImageBitmap image,
              long srcOffset,
              long srcSize,
              long dstOffset,
              long dstSize,
              Paint paint, CallbackInfo ci) {
        if (ci.getId().equals("drawImageRect-cI72Soc") && image instanceof SkiaImageImageBitmap skiaImageImageBitmap) {
            skiaImageImageBitmap.doDraw(skia, srcOffset, srcSize, dstOffset, dstSize, paint, getSkia(paint));
            ci.cancel();
        }
    }
}
