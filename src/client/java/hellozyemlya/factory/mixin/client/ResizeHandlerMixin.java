package hellozyemlya.factory.mixin.client;

import hellozyemlya.compose.MinecraftSurfaceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class ResizeHandlerMixin {
    @Inject(method = "onWindowSizeChanged", at = @At(value = "RETURN"))
    void fun(long window, int width, int height, CallbackInfo ci) {
        MinecraftSurfaceManager.Companion.getINSTANCE().resize(MinecraftClient.getInstance().getFramebuffer());
    }
}