package hellozyemlya.compose.node

import com.facebook.yoga.YogaNode

fun YogaNode.add(node: YogaNode) {
    this.addChildAt(node, this.childCount)
}


fun YogaNode.toMutableList(): MutableList<YogaNode> {
    return object : AbstractMutableList<YogaNode>() {
        override fun add(index: Int, element: YogaNode) {
            this@toMutableList.addChildAt(element, index)
        }

        override val size: Int
            get() = this@toMutableList.childCount

        override fun get(index: Int): YogaNode {
            return this@toMutableList.getChildAt(index)
        }

        override fun removeAt(index: Int): YogaNode {
            return this@toMutableList.removeChildAt(index)
        }

        override fun set(index: Int, element: YogaNode): YogaNode {
            val oldChild = this@toMutableList.getChildAt(index)
            removeAt(index)
            add(index, element)
            return oldChild
        }
    }
}