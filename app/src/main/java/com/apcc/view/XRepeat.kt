package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.apcc.emma.R
import com.apcc.utils.RepeatType

@SuppressLint("AppCompatCustomView")
class XRepeat: LinearLayout, ViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }


    private var mListener:Listener? = null

    private lateinit var lblLabel:XTextView
    private lateinit var ctlParent:ConstraintLayout
    private lateinit var rbtNone:XRadioButton
    private lateinit var rbtDaily:XRadioButton
    private lateinit var rbtWeekly:XRadioButton
    private lateinit var rbtMonthly:XRadioButton
    private lateinit var rbtYearly:XRadioButton

    private var mEnable = true
    private var repeatType = RepeatType.NONE


    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XRepeat
    override val mLayoutID: Int = R.layout.view_repeat
    override val mViewGroup: ViewGroup = this

    override fun extraTypes(typedArray: TypedArray) {
        mEnable = typedArray.getBoolean(R.styleable.XRepeat_android_enabled, true)
        repeatType = typedArray.getInt(R.styleable.XRepeat_repeatType, RepeatType.NONE)
    }

    override fun initView(root: View) {

        lblLabel = root.findViewById(R.id.lblLabel)
        ctlParent = root.findViewById(R.id.ctlParent)
        rbtNone = root.findViewById(R.id.rbtNone)
        rbtDaily = root.findViewById(R.id.rbtDaily)
        rbtWeekly = root.findViewById(R.id.rbtWeekly)
        rbtMonthly = root.findViewById(R.id.rbtMonthly)
        rbtYearly = root.findViewById(R.id.rbtYearly)

        rbtNone.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                refreshChecked(buttonView.id)
                repeatType = RepeatType.NONE
            }
        }
        rbtDaily.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                refreshChecked(buttonView.id)
                repeatType = RepeatType.DAILY
            }
        }
        rbtWeekly.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                refreshChecked(buttonView.id)
                repeatType = RepeatType.WEEKLY
            }
        }
        rbtMonthly.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                refreshChecked(buttonView.id)
                repeatType = RepeatType.MONTHLY
            }
        }
        rbtYearly.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                refreshChecked(buttonView.id)
                repeatType = RepeatType.YEARLY
            }
        }

    }

    override fun updateView() {
        ///
        refreshChecked()
        isEnabled = mEnable
    }

    override fun setEnabled(enabled: Boolean) {
        mEnable = enabled
        super.setEnabled(enabled)
        ctlParent.isEnabled = enabled
        rbtNone.isEnabled = enabled
        rbtDaily.isEnabled = enabled
        rbtWeekly.isEnabled = enabled
        rbtMonthly.isEnabled = enabled
        rbtYearly.isEnabled = enabled
    }

    fun setListener(listener: Listener){
        mListener = listener
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    private fun refreshChecked(viewID: Int){
        rbtNone.isChecked = rbtNone.id == viewID
        rbtDaily.isChecked = rbtDaily.id == viewID
        rbtWeekly.isChecked = rbtWeekly.id == viewID
        rbtMonthly.isChecked = rbtMonthly.id == viewID
        rbtYearly.isChecked = rbtYearly.id == viewID
    }

    private fun refreshChecked(){
        rbtNone.isChecked = containsFlag(RepeatType.NONE)
        rbtDaily.isChecked = containsFlag(RepeatType.DAILY)
        rbtWeekly.isChecked = containsFlag(RepeatType.WEEKLY)
        rbtMonthly.isChecked = containsFlag(RepeatType.MONTHLY)
        rbtYearly.isChecked = containsFlag(RepeatType.YEARLY)
    }

    fun setRepeatType(repeatType: Int?){
        this.repeatType = repeatType?:RepeatType.NONE
        refreshChecked()
    }

    fun getRepeatType():Int{
        return repeatType
    }


    private fun containsFlag(flag: Int): Boolean {
        return repeatType or flag == repeatType
    }

    private fun addFlag(flag: Int) {
        repeatType = repeatType or flag
    }

    private fun removeFlag(flag: Int) {
        repeatType = repeatType and flag.inv()
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    interface Listener{
    }


}