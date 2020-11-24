package com.pyamsoft.pydroid.ui.util

import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder

/**
 * Remove all item decorations from a recyclerview
 */
fun RecyclerView.removeAllItemDecorations() {
    val totalCount = this.itemDecorationCount - 1
    if (totalCount <= 0) {
        return
    }

    for (i in totalCount..0) {
        this.removeItemDecorationAt(i)
    }
}

/**
 * Call the ViewBinder.teardown() on all view holders
 */
fun RecyclerView.Adapter<*>.teardownAdapter(recyclerView: RecyclerView) {
    val itemSize = itemCount
    if (itemSize <= 0) {
        return
    }

    for (index in 0 until itemSize) {
        val holder = recyclerView.findViewHolderForAdapterPosition(index)
        if (holder is ViewBinder<*>) {
            holder.teardown()
        }
    }
}