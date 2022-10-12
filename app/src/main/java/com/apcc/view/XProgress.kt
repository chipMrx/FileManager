package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.apcc.emma.R

@SuppressLint("AppCompatCustomView")
class XProgress: ConstraintLayout, ViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }



    lateinit var lblDescription:XTextView
    lateinit var pgbProgress: ProgressBar

    private var mText:String? = ""


    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XProgress
    override val mLayoutID: Int = R.layout.view_progress
    override val mViewGroup: ViewGroup = this

    override fun extraTypes(typedArray: TypedArray) {
        mText = typedArray.getString(R.styleable.XProgress_android_text)
    }

    override fun initView(root: View) {
        lblDescription = root.findViewById(R.id.lblDescription)
        pgbProgress = root.findViewById(R.id.pgbProgress)
    }

    override fun updateView() {
        setText(mText)
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    fun setText(textContent:String?){
        mText = if (TextUtils.isEmpty(textContent)) "" else textContent
        lblDescription.text = mText

        if (TextUtils.isEmpty(textContent))
            lblDescription.visibility = View.GONE
        else
            lblDescription.visibility = View.VISIBLE
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

}