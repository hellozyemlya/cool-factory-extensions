package hellozyemlya.factory

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import net.minecraft.client.MinecraftClient
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import org.jetbrains.skia.Image

@Composable
fun Icon(image: Image, rotation: Float) {
    Image(image.toComposeImageBitmap(), "", Modifier.rotate(rotation))
}
@Composable
fun Inventory(inventory: Inventory) {
    var update by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            update++
//            println("updated")
        }
    }
    var currentRotation by remember { mutableStateOf(0f) }

    val rotation = remember { Animatable(currentRotation) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = currentRotation + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ) {
            currentRotation = value
        }
    }

    key(update) {
        LazyColumn(modifier = Modifier.width(200.dp).fillMaxHeight()) {
            items(inventory.size()) {
                val stack = inventory.getStack(it)
                val item = stack.item
                val image = ExampleModClient.itemToIcon[item]
                if (image != null) {
                    Icon(image, rotation.value)
                } else {
                    Text("Item $stack")
                }
            }
        }
    }
}

@Composable
fun App() {
    Column {
        var counter by remember { mutableStateOf(0) }
        var text by remember { mutableStateOf("Text") }
        val playerInventory = remember { MinecraftClient.getInstance().player?.inventory }
        TextField(text, { text = it })

        LaunchedEffect(Unit) {
            launch {
                while (true) {
                    delay(1000) // Delay for 1 second
                    counter++
                    text = counter.toString()
                }
            }
        }

        Button({}) {
            Text("Hello!")
        }

        Box(Modifier.weight(1f)) {
            if (playerInventory != null) {
                Inventory(playerInventory)
            }

//            val state = rememberLazyListState()
//
//            LazyColumn(state = state, modifier = Modifier.width(200.dp).fillMaxHeight()) {
//                items(100) {
//                    Text("Item $it")
//                }
//            }
//
//            VerticalScrollbar(
//                rememberScrollbarAdapter(state),
//                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
//            )
        }
    }
}