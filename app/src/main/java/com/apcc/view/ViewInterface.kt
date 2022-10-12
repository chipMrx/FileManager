package com.apcc.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface ViewInterface {
    val mLayoutID: Int
        get() = 0
    val mStyleableID: IntArray
    val mViewGroup: ViewGroup?
            get() = null

    fun parseAttributes(context: Context?, attrs: AttributeSet?){
        context?.let {
            try {
                val typedArray = context.theme.obtainStyledAttributes(attrs, mStyleableID, 0, 0)
                extraTypes(typedArray)// ****
                typedArray.recycle()
                /////////////
                // get view
                if (mLayoutID != 0){
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    initView(inflater.inflate(mLayoutID, mViewGroup)) // ****
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        updateView()// ****
    }

    /**
     * set attributes
     */
    fun extraTypes(typedArray: TypedArray){}

    /**
     * using for custom view
     */
    fun initView(root:View){}

    fun updateView()
}

