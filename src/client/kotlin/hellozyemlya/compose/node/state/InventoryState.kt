package hellozyemlya.compose.node.state

import androidx.compose.runtime.*
import kotlinx.coroutines.yield
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("cool-factory-extensions")
@Composable
fun playerInventorySlot(slotIndex: Int): State<ItemStack> {
    val state = remember { mutableStateOf(MinecraftClient.getInstance().player!!.inventory.getStack(slotIndex).copy()) }

    LaunchedEffect(slotIndex) {
        logger.info("launched effect for idx $slotIndex")
        while(true) {
            val actualValue = MinecraftClient.getInstance().player!!.inventory.getStack(slotIndex)
            val currentValue = state.value
            if(!(ItemStack.areEqual(currentValue, actualValue))) {
                logger.info("slot: $slotIndex, was ${currentValue}, set to ${actualValue}")
                state.value = actualValue.copy()
            }

            yield()
        }
    }

    return state
}