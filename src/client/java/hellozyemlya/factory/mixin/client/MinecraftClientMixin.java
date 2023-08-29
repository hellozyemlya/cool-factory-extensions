package hellozyemlya.factory.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Unique
    private boolean isConnected;

    @Inject(method = "<init>", at = @At("RETURN"))
    void subscribeForConnectionEvents(RunArgs args, CallbackInfo ci) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            isConnected = true;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            isConnected = false;
        });
    }
}
