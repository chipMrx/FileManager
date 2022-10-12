package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import com.apcc.emma.R
import com.apcc.utils.DateHelper
import com.apcc.utils.Logger
import com.apcc.utils.Util


@SuppressLint("AppCompatCustomView")
class XInput : ConstraintLayout, ViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
        parseAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        parseAttributes(context, attrs)
    }


    lateinit var ctlParent: ConstraintLayout
    lateinit var lblLabel: XTextView
    lateinit var edtInput: XEditText
    lateinit var btnRight: XButton
    lateinit var chkPasswordShow: XCheckBox

    private var mHint: String? = ""
    private var mShowRightImage = RightImage.AUTO
    private var mEnable = true
    private var mShowLabel = true
    private var mSelectAllOnFocus = false
    private var mParentAble = false
    private var mInputType = EditorInfo.TYPE_CLASS_TEXT
    private var mText: String? = ""
    private var mLabel: String? = ""
    private var mCloseIcon = R.drawable.ic_remove_circle
    private var mMaxLines = 1
    private var mMinLines = 1
    private var mMaxLength = -1 // don't limit
    private var mHandlerImeAction = EditorInfo.IME_ACTION_UNSPECIFIED
    private var mImeActionLabel: String? = ""

    private var mListener: Listener? = null

    var mTimeMillis: Long = 0
    var mFormat: String = DateHelper.FORMAT_DATE_TIME // full


    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XInput
    override val mLayoutID: Int = R.layout.view_input
    override val mViewGroup: ViewGroup = this

    override fun extraTypes(typedArray: TypedArray) {
        mHint = typedArray.getString(R.styleable.XInput_android_hint)
        mEnable = typedArray.getBoolean(R.styleable.XInput_android_enabled, true)
        mShowLabel = typedArray.getBoolean(R.styleable.XInput_showLabel, true)
        mShowRightImage = typedArray.getInt(R.styleable.XInput_showRightImage, RightImage.AUTO)
        mInputType =
            typedArray.getInt(R.styleable.XInput_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
        mSelectAllOnFocus =
            typedArray.getBoolean(R.styleable.XInput_android_selectAllOnFocus, false)
        mText = typedArray.getString(R.styleable.XInput_android_text)
        mFormat = parseFormatDate(
            typedArray.getInt(
                R.styleable.XInput_formatDateTime,
                DateFormat.DATE_TIME
            )
        )
        mLabel = typedArray.getString(R.styleable.XInput_android_label)
        mCloseIcon =
            typedArray.getResourceId(R.styleable.XInput_rightIcon, R.drawable.ic_remove_circle)
        mMaxLines = typedArray.getInt(R.styleable.XInput_android_maxLines, 1)
        mMinLines = typedArray.getInt(R.styleable.XInput_android_minLines, 1)
        mMaxLength = typedArray.getInt(R.styleable.XInput_inputMaxLength, -1)
        mHandlerImeAction = typedArray.getInt(
            R.styleable.XInput_android_imeOptions,
            EditorInfo.IME_ACTION_UNSPECIFIED
        )
        mImeActionLabel = typedArray.getString(R.styleable.XInput_android_imeActionLabel)
        mParentAble = typedArray.getBoolean(R.styleable.XInput_parentAble, false)
    }

    override fun initView(root: View) {
        ctlParent = root.findViewById(R.id.ctlParent)
        lblLabel = root.findViewById(R.id.lblLabel)
        edtInput = root.findViewById(R.id.edtInput)
        btnRight = root.findViewById(R.id.btnRight)
        chkPasswordShow = root.findViewById(R.id.chkPasswordShow)

        ctlParent.setOnClickListener {
            mListener?.onViewClick(this@XInput)
        }

        edtInput.setOnTouchListener { view, event ->
            if (mListener?.onContentClick(this@XInput) == true) {
                edtInput.clearFocus()
                true
            } else
                false
        }

        btnRight.setOnClickListener {
            if (mListener?.onRightImageClick(this) != true) {
                setText("")
                edtInput.requestFocus()
            }
        }
        chkPasswordShow.setOnCheckedChangeListener { _, _ -> updateShowPassword() }
        //
        edtInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == mHandlerImeAction) {
                mListener?.onImeActionHandled(this, actionId) ?: false
            } else mListener?.onImeAction(this, actionId) ?: false
        }

        edtInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                showHideClear()
                mListener?.onContentChanged(s?.toString() ?: "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    override fun updateView() {
        setHint(mHint)
        setEnable(mEnable)
        setSelectAllOnFocus(mSelectAllOnFocus)
        setText(mText)
        setLabel(mLabel)
        showLabel(mShowLabel)
        setMaxLength(mMaxLength)
        setMinLines(mMinLines)
        setMaxLines(mMaxLines)
        setImeAction(mImeActionLabel, mHandlerImeAction)
        setInputType(mInputType)
        setRightImage(mCloseIcon)
        setParentAble(mParentAble)

    }

    fun setListener(listener: Listener) {
        mListener = listener
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    fun setHint(hint: String?) {
        mHint = if (TextUtils.isEmpty(hint)) "" else hint
        edtInput.hint = hint
    }

    fun setEnable(enable: Boolean) {
        mEnable = enable
        edtInput.isEnabled = enable
        showHideClear()
    }

    fun setInputType(inputType: Int) {
        mInputType = inputType
        edtInput.inputType = inputType
        //EditorInfo.TYPE_CLASS_TEXT
        //EditorInfo.TYPE_DATETIME_VARIATION_DATE
    }

    fun setSelectAllOnFocus(enable: Boolean) {
        mSelectAllOnFocus = enable
        edtInput.setSelectAllOnFocus(enable)
    }

    fun setText(textContent: String?) {
        mText = if (TextUtils.isEmpty(textContent)) "" else textContent
        edtInput.setText(mText)
    }

    fun setLabel(label: String?) {
        mLabel = if (TextUtils.isEmpty(label)) "" else label
        lblLabel.text = mLabel
    }

    fun showLabel(isShow: Boolean) {
        mShowLabel = isShow
        lblLabel.visibility = if (isShow) VISIBLE else GONE
    }

    fun setMaxLines(maxLines: Int) {
        mMaxLines = maxLines
        if (maxLines >= 2) { // set text align for multiline
            edtInput.isSingleLine = false
            edtInput.gravity = Gravity.TOP
            //edtInput.maxLines = maxLines
            edtInput.maxLines = Int.MAX_VALUE
        } else {
            edtInput.gravity = Gravity.CENTER_VERTICAL
            edtInput.setLines(1)
            edtInput.isSingleLine = true
            edtInput.ellipsize = TextUtils.TruncateAt.END
        }
    }

    fun setMinLines(minLines: Int) {
        mMinLines = minLines
        edtInput.minLines = minLines
        if (minLines >= 2) { // set text align for multiline
            edtInput.gravity = Gravity.TOP
            edtInput.setLines(minLines)
        } else {
            edtInput.gravity = Gravity.CENTER_VERTICAL
        }
    }

    /**
     * just set / ignore LengthFilter
     */
    fun setMaxLength(maxLength: Int) {
        mMaxLength = maxLength
        edtInput.setMaxLength(maxLength)
    }

    fun setImeAction(imeLabel: String?, actionID: Int) {
        mHandlerImeAction = actionID
        mImeActionLabel = imeLabel
        if (!TextUtils.isEmpty(imeLabel)) {
            edtInput.setImeActionLabel(imeLabel, actionID)
        }
    }

    fun setRightImage(resource: Int) {
        mCloseIcon = resource
        btnRight.setBackgroundResource(resource)
    }

    fun showHideClear() {
        val pdSmall:Int = Util.getDimension(context, R.dimen.margin_tiny).toInt()
        val pdImage:Int = Util.getDimension(context, R.dimen.imageSmall).toInt()

        btnRight.visibility = when {
            mShowRightImage == RightImage.ALWAYS_HIDE ->{
                edtInput.setPadding(pdSmall, 0, pdSmall, 0)
                View.GONE
            }
            mShowRightImage == RightImage.ALWAYS_SHOW ->{
                edtInput.setPadding(pdSmall, 0, pdImage, 0)
                View.VISIBLE
            }
            mEnable && edtInput.text.isNotEmpty() ->{
                edtInput.setPadding(pdSmall, 0, pdImage, 0)
                View.VISIBLE
            }
            else -> {
                edtInput.setPadding(pdSmall, 0, pdSmall, 0)
                View.GONE
            }
        }

        chkPasswordShow.visibility = if (isInputPasswordType()) btnRight.visibility else View.GONE
        updateShowPassword()

    }

    fun setError(error: String?) {
        edtInput.error = if (TextUtils.isEmpty(error)) null else error
    }

    fun setError(error: String?, icon: Drawable?) {
        edtInput.setError(if (TextUtils.isEmpty(error)) null else error, icon)
    }


    /**
     * time as millis seconds
     */
    fun setTimeMillis(time: Long) {
        mTimeMillis = time
        // convert to date
        setText(DateHelper.dateTimeToString(time, mFormat))
    }

    /**
     * time as millis seconds
     */
    fun setTimeMillis(time: Long, format: String) {
        mFormat = format
        setTimeMillis(time)
    }

    fun setTimeString(time: String) {
        setTimeMillis(DateHelper.stringToDateTime(time))
    }

    fun getTimeString(): String {
        return DateHelper.dateTimeToString(mTimeMillis)
    }

    fun getText(): String {
        return edtInput.text.toString()
    }

    fun getCleanText(): String {
        return edtInput.text.toString().trim()
    }

    fun getLabel(): String {
        return lblLabel.text.toString()
    }

    fun getInputType(): Int {
        return mInputType
    }

    fun setParentAble(focusAble: Boolean) {
        mParentAble = focusAble
        ctlParent.isFocusable = focusAble
        ctlParent.isClickable = focusAble
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    private fun isInputPasswordType(): Boolean{
        return (edtInput.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD
                || (edtInput.inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD) == InputType.TYPE_NUMBER_VARIATION_PASSWORD
                || (edtInput.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                || (edtInput.inputType and InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    private fun updateShowPassword(){
        if (mListener?.onExtendButtonClick(this) != true
            && isInputPasswordType()) {
            if (chkPasswordShow.isChecked){
                edtInput.transformationMethod = null
            }else {
                edtInput.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun parseFormatDate(format: Int): String {
        return when (format) {
            DateFormat.DATE -> DateHelper.FORMAT_DATE
            DateFormat.DATE_TIME -> DateHelper.FORMAT_DATE_TIME
            DateFormat.DATE_TIME_SHORT -> DateHelper.FORMAT_DATE_TIME_SHORT
            DateFormat.TIME -> DateHelper.FORMAT_TIME
            DateFormat.TIME_SHORT -> DateHelper.FORMAT_TIME_SHORT
            else -> DateHelper.FORMAT_DATE_TIME
        }
    }

    interface Listener {
        fun onImeAction(v: View, actionID: Int): Boolean = false
        fun onImeActionHandled(v: View, actionID: Int): Boolean = false
        fun onContentClick(v: View): Boolean = false
        fun onViewClick(v: View) {}

        /**
         * default is clean text
         * return == true => cancel clean text
         */
        fun onRightImageClick(v: View): Boolean = false

        fun onExtendButtonClick(v: View): Boolean = false

        fun onContentChanged(s: String) {}
    }


    /**
     * using for right image
     */
    @IntDef(RightImage.AUTO, RightImage.ALWAYS_SHOW, RightImage.ALWAYS_HIDE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RightImage {
        companion object {
            const val AUTO = 0
            const val ALWAYS_SHOW = 1
            const val ALWAYS_HIDE = 2
        }
    }

    /**
     * using for format date time
     */
    @IntDef(
        DateFormat.DATE,
        DateFormat.DATE_TIME,
        DateFormat.DATE_TIME_SHORT,
        DateFormat.TIME,
        DateFormat.TIME_SHORT
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class DateFormat {
        companion object {
            const val DATE = 0
            const val DATE_TIME = 1
            const val DATE_TIME_SHORT = 2
            const val TIME = 3
            const val TIME_SHORT = 4
        }
    }
}