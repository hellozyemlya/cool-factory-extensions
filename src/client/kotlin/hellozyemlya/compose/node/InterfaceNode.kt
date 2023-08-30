package hellozyemlya.compose.node

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import com.facebook.yoga.*
import hellozyemlya.compose.ClientTickDispatcher
import kotlinx.coroutines.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import kotlin.coroutines.CoroutineContext

@DslMarker
annotation class SceneScopeMarker

@Stable
fun interface Renderer {
    fun DrawContext.render(node: InterfaceNode)
}

fun interface MeasureFunction {
    @Stable
    fun measure(
        node: InterfaceNode, width: Float,
        widthMode: YogaMeasureMode,
        height: Float,
        heightMode: YogaMeasureMode
    ): Pair<Float, Float>
}

val EmptyRenderer = Renderer { }

interface InterfaceNode {
    var renderer: Renderer
    var measureFunction: MeasureFunction?
    var flex: Flex
    val layoutNode: YogaNode

    companion object {
        val Constructor: () -> InterfaceNode = ::Node
    }
}

class Node : InterfaceNode {
    override var renderer = EmptyRenderer
    override var measureFunction: MeasureFunction? = null
        set(value) {
            field = value
            if(field == null) {
                layoutNode.setMeasureFunction(null)
            } else {
                layoutNode.setMeasureFunction(::measure)
                layoutNode.dirty()
            }
        }
    override var flex: Flex = Flex
        get() = field
        set(value) {
            field = value
            field.apply(layoutNode)
        }

    val children = mutableListOf<Node>()
    var parent: Node? = null

    override val layoutNode: YogaNode = YogaNodeFactory.create().apply {
        this.data = this@Node
    }

    fun render(context: DrawContext) {
        renderer.apply {
            context.render(this@Node)
        }
        children.forEach {
            it.render(context)
        }
    }

    private fun measure(
        node: YogaNode, width: Float,
        widthMode: YogaMeasureMode,
        height: Float,
        heightMode: YogaMeasureMode
    ): Long {
        val func = measureFunction
        return if (func == null) {
            YogaMeasureOutput.make(-1, -1)
        } else {
            val (w, h) = func.measure(node.data as InterfaceNode, width, widthMode, height, heightMode)
            YogaMeasureOutput.make(w, h)
        }
    }
}

class NodeApplier(root: Node) : AbstractApplier<Node>(root) {
    override fun insertBottomUp(index: Int, instance: Node) {
        check(instance.parent == null) {
            "$instance must not have a parent when being inserted."
        }
        instance.parent = current
        current.children.add(index, instance)
        current.layoutNode.addChildAt(instance.layoutNode, index)
    }

    override fun insertTopDown(index: Int, instance: Node) {

    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
        current.layoutNode.toMutableList().move(from, to, count)
    }

    override fun onClear() {
        current.children.clear()
        current.layoutNode.toMutableList().clear()
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
        current.layoutNode.toMutableList().remove(index, count)
    }

}

@Composable
inline fun Node(
    flex: Flex = Flex,
    renderer: Renderer = EmptyRenderer,
    measureFunction: MeasureFunction? = null,
    content: @Composable () -> Unit = {}
) {
    ComposeNode<InterfaceNode, NodeApplier>(
        factory = InterfaceNode.Constructor,
        update = {
            set(renderer) { this.renderer = it }
            set(measureFunction) { this.measureFunction = it }
            set(flex) { this.flex = it }
        },
        content = content
    )
}


@SceneScopeMarker
class GuiScene : CoroutineScope {
    var hasFrameWaiters = false
    private val rootNode = Node()
    var running = false
    val clock = BroadcastFrameClock { hasFrameWaiters = true }
    val composeScope = CoroutineScope(ClientTickDispatcher) + clock
    override val coroutineContext: CoroutineContext = composeScope.coroutineContext
    private val recomposer = Recomposer(coroutineContext)
    private val composition = Composition(NodeApplier(rootNode), recomposer)

    var applyScheduled = false
    val snapshotHandle = Snapshot.registerGlobalWriteObserver {
        if (!applyScheduled) {
            applyScheduled = true
            composeScope.launch {
                applyScheduled = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    var exitScheduled = false

    fun exit() {
        exitScheduled = true
    }

    fun start(content: @Composable GuiScene.() -> Unit) {
        !running || return
        running = true

        launch {
            recomposer.runRecomposeAndApplyChanges()
        }

        launch {
            setContent(content)
            while (!exitScheduled) {
                if (hasFrameWaiters) {
                    hasFrameWaiters = false
                }
                delay(50)
            }
            running = false
            recomposer.close()
            snapshotHandle.dispose()
            composition.dispose()
            composeScope.cancel()
        }
    }

    private fun setContent(content: @Composable GuiScene.() -> Unit) {
        hasFrameWaiters = true
        composition.setContent {
            content()
        }
    }

    fun render(drawContext: DrawContext) {
        clock.sendFrame(System.nanoTime())
        val window = MinecraftClient.getInstance().window
        rootNode.layoutNode.calculateLayout(window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
        rootNode.render(drawContext)
    }
}

fun scene(
    content: @Composable GuiScene.() -> Unit
): GuiScene {
    return GuiScene().apply {
        start(content)
    }
}