package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.apcc.emma.R
import com.apcc.utils.DataHelper
import com.apcc.utils.TextType
import com.apcc.utils.Util


@SuppressLint("AppCompatCustomView")
class XEditText:EditText, ViewInterface, TextInterface {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
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
    override val mStyleableID: IntArray = R.styleable.XEditText

    override fun extraTypes(typedArray: TypedArray) {
        mTextType = typedArray.getInteger(R.styleable.XEditText_textType, TextType.TEXT_NORMAL)
        mTextColorResource = typedArray.getResourceId(
            R.styleable.XEditText_android_textColor,
            R.color.txt_tint
        )
        mHintColorResource = typedArray.getResourceId(
            R.styleable.XEditText_android_textColorHint,
            R.color.colorHint
        )
    }

    override fun updateView() {
        setTextType(mTextType, mTextColorResource)
        setTextHintColorResource(mHintColorResource)
        imeOptions = EditorInfo.IME_ACTION_DONE
        compoundDrawablePadding = Util.getDimension(context, R.dimen.margin_small).toInt()
    }
    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    override fun setText(text: CharSequence?, type: BufferType?) {
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return isEnabled && super.onTouchEvent(event)
    }

    fun setMaxLength(maxLength: Int){
        val listFilters = ArrayList<InputFilter>()
        for (filter in filters){
            if (filter !is InputFilter.LengthFilter) { //ignore filter length
                listFilters.add(filter)
            }
        }
        if (maxLength > 0) {// add filter length
            listFilters.add(InputFilter.LengthFilter(maxLength))
        }
        filters = listFilters.toTypedArray()
    }
}