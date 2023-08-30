package hellozyemlya.factory

import androidx.compose.runtime.*
import com.facebook.yoga.*
import hellozyemlya.compose.ClientTickDispatcher
import hellozyemlya.compose.node.*
import hellozyemlya.compose.node.components.McItem
import hellozyemlya.compose.node.components.McText
import hellozyemlya.compose.node.components.McTexture
import hellozyemlya.compose.node.state.playerInventorySlot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.registry.Registries

@Composable
fun MyHud() {
    var counter by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("counter: $counter") }
//    LaunchedEffect(Unit) {
//        launch {
//            while (true) {
//                counter++
//                text = "counter: $counter"
//                delay(5000)
//            }
//        }
//    }
    Node(
        Flex
            .direction(FlexDirection.ROW)
            .justifyContent(Justify.FLEX_START)
            .wrap(Wrap.WRAP)
    ) {
        (0 until MinecraftClient.getInstance().player!!.inventory.size()).forEach { idx ->
            key(idx) {
                val stack by playerInventorySlot(idx)
                McItem(stack, 32)
            }
        }
//        Registries.ITEM.forEach {
//            if (it.isFood) {
//                McItem(it.defaultStack, 32)
//            }
//        }
        McText(text)
        if (counter % 2 == 0) {
            McText("Counter is multiple of 2")
        }
        McItem(Items.ACACIA_BOAT.defaultStack, 128)
        McText("Hello World 123")
    }
}

fun YogaNode.createChild(): YogaNode {
    val node = YogaNodeFactory.create()
    this.add(node)
    return node
}

object ExampleModClient : ClientModInitializer {
    private val scene by lazy {
        scene {
            MyHud()
        }
    }

    override fun onInitializeClient() {
        ClientTickDispatcher.setup()
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
        }
        HudRenderCallback.EVENT.register { ctx, delta ->
            scene.render(ctx)
        }
    }
}

