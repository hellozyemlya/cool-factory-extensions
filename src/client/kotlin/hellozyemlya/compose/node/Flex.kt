package hellozyemlya.compose.node

import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaNode
import com.facebook.yoga.YogaWrap


interface Flex {
    fun apply(node: YogaNode)
    infix fun then(other: Flex): Flex =
        if (other === Flex) this else CombinedFlex(this, other)

    companion object : Flex {
        override fun apply(node: YogaNode) {
        }

        override infix fun then(other: Flex): Flex {
            return other
        }
    }
}
internal class CombinedFlex(private val current: Flex, private val next: Flex) : Flex {
    override fun apply(node: YogaNode) {
        current.apply(node)
        next.apply(node)
    }

    override infix fun then(other: Flex): Flex {
        return if (other === Flex) this else CombinedFlex(this, other)
    }
}

enum class FlexDirection : Flex {
    COLUMN {
        override fun apply(node: YogaNode) {
            node.flexDirection = YogaFlexDirection.COLUMN
        }
    },
    COLUMN_REVERSE {
        override fun apply(node: YogaNode) {
            node.flexDirection = YogaFlexDirection.COLUMN_REVERSE
        }
    },
    ROW {
        override fun apply(node: YogaNode) {
            node.flexDirection = YogaFlexDirection.ROW        }
    },
    ROW_REVERSE {
        override fun apply(node: YogaNode) {
            node.flexDirection = YogaFlexDirection.ROW_REVERSE
        }
    };
}
fun Flex.direction(direction: FlexDirection) : Flex {
    return this.then(direction)
}

enum class Align(val value: Int) {
    AUTO(0),
    FLEX_START(1),
    CENTER(2),
    FLEX_END(3),
    STRETCH(4),
    BASELINE(5),
    SPACE_BETWEEN(6),
    SPACE_AROUND(7);
}
fun Flex.alignItems(align: Align) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.alignItems = YogaAlign.fromInt(align.value)
        }
    })
}

fun Flex.alignContent(align: Align) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.alignContent = YogaAlign.fromInt(align.value)
        }
    })
}

enum class Justify(val value: Int) {
    FLEX_START(0),
    CENTER(1),
    FLEX_END(2),
    SPACE_BETWEEN(3),
    SPACE_AROUND(4),
    SPACE_EVENLY(5);
}

fun Flex.justifyContent(justify: Justify) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.justifyContent = YogaJustify.fromInt(justify.value)
        }
    })
}

@JvmInline
value class Percent(val value: Float)

interface Auto {
    companion object : Auto
}
fun Flex.basis(value: Float) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.flex = value
        }
    })
}

fun Flex.basis(value: Percent) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setFlexBasisPercent(value.value)
        }
    })
}

fun Flex.basis(value: Auto) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setFlexBasisAuto()
        }
    })
}

fun Flex.grow(value: Float) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.flexGrow = value
        }
    })
}

fun Flex.shrink(value: Float) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.flexShrink = value
        }
    })
}

fun Flex.width(value: Float) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setWidth(value)
        }
    })
}

fun Flex.width(value: Percent) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setWidthPercent(value.value)
        }
    })
}

fun Flex.width(auto: Auto) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setWidthAuto()
        }
    })
}

fun Flex.height(value: Float) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setHeight(value)
        }
    })
}

fun Flex.height(value: Percent) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setHeightPercent(value.value)
        }
    })
}

fun Flex.height(auto: Auto) : Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.setHeightAuto()
        }
    })
}

enum class Wrap(val value: Int) {
    NO_WRAP(0),
    WRAP(1),
    WRAP_REVERSE(2);
}

fun Flex.wrap(wrap: Wrap): Flex {
    return this.then(object : Flex {
        override fun apply(node: YogaNode) {
            node.wrap = YogaWrap.fromInt(wrap.value)
        }
    })
}