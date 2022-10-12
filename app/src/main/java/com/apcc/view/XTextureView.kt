package com.apcc.view

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class XTextureView(context: Context, attrs: AttributeSet?, defStyle: Int) : TextureView(context, attrs, defStyle) {
    private var mRatioWidth:Int = 0
    private var mRatioHeight:Int = 0

    constructor(context: Context) : this(context, null) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initView(context)
    }
    init {
        initView(context)
    }

    private fun initView(context: Context?) {}

    fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }
}