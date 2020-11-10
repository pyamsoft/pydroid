package com.pyamsoft.pydroid.ui.util

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeAllItemDecorations() {
    val totalCount = this.itemDecorationCount - 1
    if (totalCount <= 0) {
        return
    }

    for (i in totalCount..0) {
        this.removeItemDecorationAt(i)
    }
}
