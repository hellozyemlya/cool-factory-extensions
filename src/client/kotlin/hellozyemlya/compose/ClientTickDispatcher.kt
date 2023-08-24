package hellozyemlya.compose

import kotlinx.coroutines.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext

/**
 * Executes coroutines inside minecraft client render thread on tick start.
 */
object ClientTickDispatcher : CoroutineDispatcher() {
    private val tasks = mutableListOf<Runnable>()
    private val tasksCopy = mutableListOf<Runnable>()

    fun tick() {
        synchronized(tasks) {
            tasksCopy.addAll(tasks)
            tasks.clear()
        }
        for (runnable in tasksCopy) {
            runnable.run()
        }
        tasksCopy.clear()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        synchronized(tasks) {
            tasks.add(block)
        }
    }

    fun setup() {
        ClientTickEvents.START_CLIENT_TICK.register {
            tick()
        }
    }
}

/**
 * Schedules coroutine to be executed during client tick.
 */
fun tickLaunch(block: CoroutineScope.() -> Unit): Job {
    return runBlocking(ClientTickDispatcher) {
        launch {
            block()
        }
    }
}