package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView
import com.apcc.emma.R
import com.apcc.utils.DataHelper
import com.apcc.utils.TextType

@SuppressLint("AppCompatCustomView")
class XTextView:TextView, ViewInterface, TextInterface {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }

    constructor(context: Context?,attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes){
        parseAttributes(context, attrs)
    }

    companion object{

    }


    private var mTextType:Int = TextType.TEXT_NORMAL
    private var mTextColorResource:Int = R.color.colorNormal
    private var mHintColorResource:Int = R.color.colorHint


    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XTextView

    override fun extraTypes(typedArray: TypedArray) {
        mTextType = typedArray.getInteger(R.styleable.XTextView_textType, TextType.TEXT_NORMAL)
        mTextColorResource = typedArray.getResourceId(R.styleable.XTextView_android_textColor, 0)
        mHintColorResource = typedArray.getResourceId(R.styleable.XTextView_android_textColorHint, R.color.hint_tint)
    }

    override fun updateView() {
        setTextType(mTextType, mTextColorResource)

        setTextHintColorResource(mHintColorResource)
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    override fun setText(text: CharSequence, type:BufferType){
        when(mTextType){
            TextType.TEXT_HTML -> super.setText(DataHelper.textToHtml(text.toString()), type)
            else -> super.setText(text, type)
        }
    }


    override fun getTextView(): TextView = this

    override fun refreshText() {
        this.text = text
    }

    override fun setTextType(@TextType textType: Int, requestColorResource: Int){
        mTextType = textType
        super.setTextType(textType, requestColorResource)
    }


    override fun setTextColorResource(colorResource: Int) {
        mTextColorResource = colorResource
        super.setTextColorResource(colorResource)
    }
}