package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.apcc.emma.R
import com.apcc.utils.Gender

@SuppressLint("AppCompatCustomView")
class XGender: ConstraintLayout, ViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }

    companion object{

    }

    lateinit var lblLabel:XTextView
    lateinit var rabUnisex:RadioButton
    lateinit var rabMale:RadioButton
    lateinit var rabFemale:RadioButton
    lateinit var ragGroup:RadioGroup

    private var mListener:Listener? = null

    private var mGender = Gender.UNISEX
    private var mLabel:String? = ""
    private var mEnable = true

    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XGender
    override val mLayoutID: Int = R.layout.view_gender
    override val mViewGroup: ViewGroup = this

    override fun extraTypes(typedArray: TypedArray) {
        mGender = typedArray.getInteger(R.styleable.XGender_gender, Gender.UNISEX)
        mLabel = typedArray.getString(R.styleable.XGender_android_label)
        mEnable = typedArray.getBoolean(R.styleable.XGender_android_enabled, true)
    }

    override fun initView(root: View) {
        lblLabel = root.findViewById(R.id.lblLabel)
        rabUnisex = root.findViewById(R.id.rabUnisex)
        rabMale = root.findViewById(R.id.rabMale)
        rabFemale = root.findViewById(R.id.rabFemale)
        ragGroup = root.findViewById(R.id.ragGroup)

        rabUnisex.setOnCheckedChangeListener { _, isChecked -> if (isChecked) mGender = Gender.UNISEX }
        rabMale.setOnCheckedChangeListener { _, isChecked -> if (isChecked) mGender = Gender.MALE  }
        rabFemale.setOnCheckedChangeListener { _, isChecked -> if (isChecked) mGender = Gender.FEMALE  }
    }

    override fun updateView() {
        setLabel(mLabel?:context.getString(R.string.lbl_gender))
        setGender(mGender)
        setEnable(mEnable)
    }

    fun setListener(listener: Listener){
        mListener = listener
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    fun setLabel(label: String){
        mLabel = label
        lblLabel.text = label
    }

    fun getGender() = mGender

    fun setGender(@Gender gender: Int){
        mGender = gender
        rabUnisex.isChecked = gender == Gender.UNISEX
        rabMale.isChecked = gender == Gender.MALE
        rabFemale.isChecked = gender == Gender.FEMALE

    }

    fun setEnable(enable:Boolean){
        mEnable = enable
        rabUnisex.isEnabled = enable
        rabMale.isEnabled = enable
        rabFemale.isEnabled = enable

        ragGroup.isEnabled = enable
    }

    //////////////////////////////////////////////////
    ///
    //////////////////////////////////////////////////
    interface Listener{
        fun genderChange(@Gender gender:Int)
    }

}