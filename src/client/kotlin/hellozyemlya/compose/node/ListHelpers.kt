package hellozyemlya.compose.node

fun <T> MutableList<T>.move(from: Int, to: Int, count: Int) {
    val dest = if (from > to) to else to - count
    if (count == 1) {
        if (from == to + 1 || from == to - 1) {
            // Adjacent elements, perform swap to avoid backing array manipulations.
            val fromEl = get(from)
            val toEl = set(to, fromEl)
            set(from, toEl)
        } else {
            val fromEl = removeAt(from)
            add(dest, fromEl)
        }
    } else {
        val subView = subList(from, from + count)
        val subCopy = subView.toMutableList()
        subView.clear()
        addAll(dest, subCopy)
    }
}

fun <T> MutableList<T>.remove(index: Int, count: Int) {
    if (count == 1) {
        removeAt(index)
    } else {
        subList(index, index + count).clear()
    }
}