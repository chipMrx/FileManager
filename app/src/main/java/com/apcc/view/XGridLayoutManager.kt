package com.apcc.view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.apcc.base.adapter.BaseAdapter

class XGridLayoutManager(
    var context: Context,
    var adapter: BaseAdapter<*,*>,
    spanCount: Int,
    orientation: Int,
    reverseLayout: Boolean
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {



    init {
        spanSizeLookup = XSpanSizeLookup()
    }

    ////////////////////////////////////////
    inner class XSpanSizeLookup: SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            if (adapter.itemCount > position){
                if (adapter.getItemViewType(position) == BaseAdapter.ViewType.HEADER){
                    return spanCount
                }
            }
            return 1
        }
    }
}