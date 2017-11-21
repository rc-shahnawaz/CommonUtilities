package com.shaizy.utilities

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by syed.shahnawaz on 11/21/2017.
 *
 */

class EndScrollListener(private val recyclerView: RecyclerView, private val refresh: () -> Unit) {
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (recyclerView == null || recyclerView.layoutManager == null || recyclerView.adapter == null)
                return

            if (recyclerView.layoutManager.lastVisiblePosition() == recyclerView.adapter.itemCount - 1)
                refresh()
        }
    }

    init {
        recyclerView.addOnScrollListener(scrollListener)
    }
}

fun <T : RecyclerView.LayoutManager> T.lastVisiblePosition(): Int =
        when (this) {
            is LinearLayoutManager -> this.findLastCompletelyVisibleItemPosition()
            is GridLayoutManager -> this.findLastCompletelyVisibleItemPosition()
            else -> 0
        }