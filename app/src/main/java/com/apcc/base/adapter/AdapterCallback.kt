package com.apcc.base.adapter

import android.view.View

interface AdapterCallback {
    fun onClick(view: View?, position: Int)
    fun onLongClick(view: View?, position: Int):Boolean
    fun getSelectionMode():Int
    fun isSelected(position:Int):Boolean
}