package hellozyemlya.factory

import androidx.compose.runtime.*
import com.facebook.yoga.*
import hellozyemlya.compose.ClientTickDispatcher
import hellozyemlya.compose.node.Node
import hellozyemlya.compose.node.Renderer
import hellozyemlya.compose.node.add
import hellozyemlya.compose.node.components.Text
import hellozyemlya.compose.node.scene
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient

@Composable
fun MyHood() {
    var counter by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("counter: $counter") }
    LaunchedEffect(Unit) {
        launch {
            while (true) {
                counter++
                text = "counter: $counter"
                delay(1000)
            }
        }
    }

    Text(text)
    Text("Hello World 123")
}

fun YogaNode.createChild(): YogaNode {
    val node = YogaNodeFactory.create()
    this.add(node)
    return node
}

object ExampleModClient : ClientModInitializer {
    private val scene by lazy {
        scene {
            MyHood()
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

