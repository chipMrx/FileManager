package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.apcc.emma.R
import com.apcc.utils.DataHelper
import com.apcc.utils.Logger


@SuppressLint("AppCompatCustomView")
class XInputNumber : LinearLayout, ViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
        parseAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        parseAttributes(context, attrs)
    }


    lateinit var ctlParent: LinearLayout
    lateinit var lblLabel: XTextView
    lateinit var txtInput: XTextView
    lateinit var btnMinus: XButton
    lateinit var btnPlus: XButton

    private var mEnable = true
    private var mVal: Double = 0.0
    private var mLabel: String? = ""
    private var mHint: String? = ""

    private var mListener: Listener? = null

    private var mStep: Double = 1.0


    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XInputNumber
    override val mLayoutID: Int = R.layout.view_input_number
    override val mViewGroup: ViewGroup = this

    override fun extraTypes(typedArray: TypedArray) {
        mEnable = typedArray.getBoolean(R.styleable.XInput_android_enabled, true)

        mVal = typedArray.getFloat(R.styleable.XInputNumber_android_value, 0f).toDouble()
        mLabel = typedArray.getString(R.styleable.XInputNumber_android_label)
        mHint = typedArray.getString(R.styleable.XInputNumber_android_hint)
        mStep = typedArray.getFloat(R.styleable.XInputNumber_increaseStep, 1f).toDouble()

    }

    override fun initView(root: View) {
        ctlParent = root.findViewById(R.id.ctlParent)
        lblLabel = root.findViewById(R.id.lblLabel)
        txtInput = root.findViewById(R.id.txtInput)
        btnMinus = root.findViewById(R.id.btnMinus)
        btnPlus = root.findViewById(R.id.btnPlus)

        txtInput.setOnClickListener { mListener?.onContentClick(it) }

        btnMinus.setOnTouchListener{ _, e -> cancelAutoIncrease(e)}
        btnPlus.setOnTouchListener{ _, e -> cancelAutoIncrease(e)}

        btnMinus.setOnClickListener{ minus() }
        btnPlus.setOnClickListener{ plus() }

        btnMinus.setOnLongClickListener{ autoIncrease(it) }
        btnPlus.setOnLongClickListener{ autoIncrease(it) }

    }

    override fun updateView() {
        setEnable(mEnable)
        setValue(mVal)
        setLabel(mLabel)
        setHint(mHint)
    }

    fun setListener(listener: Listener) {
        mListener = listener
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    fun setEnable(enable: Boolean) {
        mEnable = enable
        txtInput.isEnabled = enable
    }

    fun setValue(value: Double) {
        mVal = value
        txtInput.text = DataHelper.numberAsText(value)
    }
    fun setHint(hint:String?) {
        mHint = hint
        txtInput.hint = hint
    }

    fun setLabel(label: String?) {
        mLabel = if (TextUtils.isEmpty(label)) "" else label
        lblLabel.text = mLabel
        lblLabel.visibility = if (TextUtils.isEmpty(label)) GONE else VISIBLE
    }

    fun setError(error: String?) {
        txtInput.error = if (TextUtils.isEmpty(error)) null else error
    }

    fun setError(error: String?, icon: Drawable?) {
        txtInput.setError(if (TextUtils.isEmpty(error)) null else error, icon)
    }


    fun getText(): String {
        return txtInput.text.toString()
    }

    private fun increase(increaseVal:Double){
        setValue(mVal + increaseVal)
    }
    private fun minus(){
        increase(-mStep)
        mListener?.onMinusTouch(mVal)
    }

    private fun plus(){
        increase(mStep)
        mListener?.onPlusTouch(mVal)
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    private fun cancelAutoIncrease(event: MotionEvent):Boolean{
        Logger.e("NGB ${event.action}")
        if (event.action == MotionEvent.ACTION_UP) {
            handler.removeMessages(AUTO_INCREASE_JOB)
        }
        return false
    }

    private fun autoIncrease(view:View):Boolean{
        handler.post(object : Runnable {
            override fun run() {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                when (view) {
                    btnMinus ->{ minus() }
                    btnPlus -> { plus() }
                }
                val message = Message.obtain(handler, this)
                message.what = AUTO_INCREASE_JOB
                handler.sendMessageDelayed(message, DELAY)
            }
        })
        return true
    }

    companion object{
        const val AUTO_INCREASE_JOB = 264776247
        const val DELAY = 150L
    }

    interface Listener {
        fun onContentClick(v: View)
        fun onMinusTouch( value:Double )
        fun onPlusTouch( value:Double )
    }


}