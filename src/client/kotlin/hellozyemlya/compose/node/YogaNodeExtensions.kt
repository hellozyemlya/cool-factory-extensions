package hellozyemlya.compose.node

import com.facebook.yoga.YogaNode

fun YogaNode.add(node: YogaNode) {
    this.addChildAt(node, this.childCount)
}