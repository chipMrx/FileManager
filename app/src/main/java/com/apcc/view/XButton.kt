package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.Button
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.apcc.emma.R

@SuppressLint("AppCompatCustomView")
class XButton:Button, ViewInterface {
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


    //button type: unset: / normal:orange / success:green / important:blue
    @IntDef(BtnType.BUTTON_UNSET, BtnType.BUTTON_NORMAL, BtnType.BUTTON_SUCCESS, BtnType.BUTTON_IMPORTANT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class BtnType{
        companion object{
            const val BUTTON_UNSET = 0
            const val BUTTON_NORMAL = 1
            const val BUTTON_SUCCESS = 2
            const val BUTTON_IMPORTANT = 3
            const val BUTTON_ICON = 4
        }
    }


    private var mButtonType = BtnType.BUTTON_UNSET

    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XButton

    override fun extraTypes(typedArray: TypedArray) {
        mButtonType = typedArray.getInteger(R.styleable.XButton_buttonType, BtnType.BUTTON_UNSET)
    }

    override fun updateView() {
        setButtonType(mButtonType)
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    fun setButtonType(@BtnType btnType:Int){
        mButtonType = btnType
        compoundDrawableTintList = ContextCompat.getColorStateList(context, R.color.btn_tint)
        setTextColor(ContextCompat.getColorStateList(context, R.color.btn_tint))
        when(btnType){
            BtnType.BUTTON_SUCCESS->{
                setBackgroundResource(R.drawable.btn_success)
            }
            BtnType.BUTTON_IMPORTANT->{
                setBackgroundResource(R.drawable.btn_dangerous)
            }
            BtnType.BUTTON_NORMAL->{
                setBackgroundResource(R.drawable.btn_normal)
            }
            BtnType.BUTTON_ICON->{
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.btn_tint)
            }
            else ->{
                // else do nothing
                //setBackgroundResource(R.drawable.btn_normal)
            }
        }
    }

}