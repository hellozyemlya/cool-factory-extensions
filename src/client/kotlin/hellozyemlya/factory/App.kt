package hellozyemlya.factory

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import net.minecraft.client.MinecraftClient
import net.minecraft.inventory.Inventory

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

    key(update) {
        LazyColumn(modifier = Modifier.width(200.dp).fillMaxHeight()) {
            items(inventory.size()) {
                Text("Item ${inventory.getStack(it)}")
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
            if(playerInventory != null) {
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