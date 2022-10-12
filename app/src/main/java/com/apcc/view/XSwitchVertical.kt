package com.apcc.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION_CODES
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.FloatProperty
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import android.widget.Switch
import androidx.annotation.DrawableRes
import androidx.annotation.InspectableProperty
import androidx.core.view.ViewCompat
import com.apcc.emma.R
import com.apcc.utils.Util
import kotlin.math.abs
import kotlin.math.ceil

@SuppressLint("AppCompatCustomView")
class XSwitchVertical : CompoundButton {

    private val THUMB_ANIMATION_DURATION = 250

    private val TOUCH_MODE_IDLE = 0
    private val TOUCH_MODE_DOWN = 1
    private val TOUCH_MODE_DRAGGING = 2

    private var mThumbDrawable: Drawable? = null
    private var mTrackDrawable: Drawable? = null

    private var mThumbTextPadding = 0

    private var mSwitchMinWidth = 0
    private var mSwitchPadding = 0
    private var mTextOn: CharSequence? = null
    private var mTextOff: CharSequence? = null
    private var mShowText = false
    private var mUseFallbackLineSpacing = false

    private var mTouchMode = 0
    private var mTouchSlop = 0
    private var mTouchX = 0f
    private var mTouchY = 0f
    private val mVelocityTracker = VelocityTracker.obtain()
    private var mMinFlingVelocity = 0

    private var mThumbPosition = 0f
    private var mSwitchWidth = 0
    private var mSwitchHeight = 0
    private var mThumbWidth = 0
    private var mTextMaxWidth = 0

    /** Left bound for drawing the switch track and thumb.  */
    private var mSwitchLeft = 0

    /** Top bound for drawing the switch track and thumb.  */
    private var mSwitchTop = 0

    /** Right bound for drawing the switch track and thumb.  */
    private var mSwitchRight = 0

    /** Bottom bound for drawing the switch track and thumb.  */
    private var mSwitchBottom = 0

    private var mTextOnPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var mTextOnColors: ColorStateList? = null

    private var mTextOffPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var mTextOffColors: ColorStateList? = null

    private var mOnLayout: Layout? = null
    private var mOffLayout: Layout? = null
    private var mPositionAnimator: ObjectAnimator? = null

    private val mTempRect = Rect()
    private var mTextBoundLeft:Float = 0f
    private var mTextBoundRight:Float = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes){

        mTextOnPaint.density = resources.displayMetrics.density
        mTextOffPaint.density = resources.displayMetrics.density

        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.XSwitchVertical, defStyleAttr, defStyleRes)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.Q) {
            saveAttributeDataForStyleable(context, R.styleable.XSwitchVertical, attrs, typedArray, defStyleAttr, defStyleRes)
        }
        if(typedArray.hasValue(R.styleable.XSwitchVertical_android_thumb)) {
            mThumbDrawable = typedArray.getDrawable(R.styleable.XSwitchVertical_android_thumb)
        }else{
            mThumbDrawable = Util.getDrawable(context, R.drawable.switch_thumb)
        }
        mThumbDrawable?.callback = this
        if (typedArray.hasValue(R.styleable.XSwitchVertical_android_track)) {
            mTrackDrawable = typedArray.getDrawable(R.styleable.XSwitchVertical_android_track)
        }else{
            mTrackDrawable = Util.getDrawable(context, R.drawable.switch_track)
        }
        mTrackDrawable?.callback = this

        mTextOn = typedArray.getText(R.styleable.XSwitchVertical_android_textOn)
        mTextOff = typedArray.getText(R.styleable.XSwitchVertical_android_textOff)
        mShowText = typedArray.getBoolean(R.styleable.XSwitchVertical_android_showText, true)
        mThumbTextPadding = typedArray.getDimensionPixelSize(R.styleable.XSwitchVertical_android_thumbTextPadding, 0)

        mSwitchMinWidth = typedArray.getDimensionPixelSize(R.styleable.XSwitchVertical_android_switchMinWidth, 0)
        mSwitchPadding = typedArray.getDimensionPixelSize(R.styleable.XSwitchVertical_android_switchPadding, 0)

        mUseFallbackLineSpacing = context.applicationInfo.targetSdkVersion >= VERSION_CODES.P

        setSwitchTextStyle(typedArray)

        typedArray.recycle()

        val config = ViewConfiguration.get(context)
        mTouchSlop = config.scaledTouchSlop
        mMinFlingVelocity = config.scaledMinimumFlingVelocity

        // Refresh display with current params

        refreshDrawableState()
        setDefaultStateDescription()
        // Default state is derived from on/off-text, so state has to be updated when on/off-text
        // are updated.
//        isChecked = isChecked
        isClickable = true
        focusable = FOCUSABLE_AUTO
    }

    /**
     * Sets the switch text color, size, style, hint color, and highlight color
     * from the specified TextAppearance resource.
     *
     * @attr ref android.R.styleable#Switch_switchTextAppearance
     */
    fun setSwitchTextStyle(typedArray: TypedArray) {
        mTextOnPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        mTextOffPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        val onColor: ColorStateList? = typedArray.getColorStateList(R.styleable.XSwitchHorizontal_textOnColor)
        mTextOnColors = onColor ?: textColors

        val offColor: ColorStateList? = typedArray.getColorStateList(R.styleable.XSwitchHorizontal_textOffColor)
        mTextOffColors = offColor ?: textColors

        val textOnSize: Int = typedArray.getDimensionPixelSize(R.styleable.XSwitchHorizontal_textOnSize, 0)
        if (textOnSize != 0) {
            if (textOnSize.toFloat() != mTextOnPaint.textSize) {
                mTextOnPaint.textSize = textOnSize.toFloat()
            }
        }
        val textOffSize: Int = typedArray.getDimensionPixelSize(R.styleable.XSwitchHorizontal_textOffSize, 0)
        if (textOffSize != 0) {
            if (textOffSize.toFloat() != mTextOffPaint.textSize) {
                mTextOffPaint.textSize = textOffSize.toFloat()
            }
        }
        requestLayout()
    }

    fun setSwitchPadding(pixels: Int) {
        mSwitchPadding = pixels
        requestLayout()
    }

    fun getSwitchPadding(): Int {
        return mSwitchPadding
    }

    fun setSwitchMinWidth(pixels: Int) {
        mSwitchMinWidth = pixels
        requestLayout()
    }

    fun getSwitchMinWidth(): Int {
        return mSwitchMinWidth
    }

    fun setTrackDrawable(track: Drawable?) {
        if (mTrackDrawable != null) {
            mTrackDrawable!!.callback = null
        }
        mTrackDrawable = track
        if (track != null) {
            track.callback = this
        }
        requestLayout()
    }

    fun setTrackResource(@DrawableRes resId: Int) {
        setTrackDrawable(context.getDrawable(resId)!!)
    }

    @InspectableProperty(name = "track")
    fun getTrackDrawable(): Drawable? {
        return mTrackDrawable
    }

    fun setThumbDrawable(thumb: Drawable?) {
        if (mThumbDrawable != null) {
            mThumbDrawable!!.callback = null
        }
        mThumbDrawable = thumb
        thumb?.callback = this
        requestLayout()
    }

    fun setThumbResource(@DrawableRes resId: Int) {
        setThumbDrawable(context.getDrawable(resId))
    }

    fun getThumbDrawable(): Drawable? {
        return mThumbDrawable
    }

    fun getTextOn(): CharSequence? {
        return mTextOn
    }

    fun setTextOn(textOn: CharSequence) {
        mTextOn = textOn
        requestLayout()
        // Default state is derived from on/off-text, so state has to be updated when on/off-text
        // are updated.
        setDefaultStateDescription()
    }

    fun getTextOff(): CharSequence? {
        return mTextOff
    }

    fun setTextOff(textOff: CharSequence) {
        mTextOff = textOff
        requestLayout()
        // Default state is derived from on/off-text, so state has to be updated when on/off-text
        // are updated.
        setDefaultStateDescription()
    }

    fun setShowText(showText: Boolean) {
        if (mShowText != showText) {
            mShowText = showText
            requestLayout()
        }
    }

    fun getShowText(): Boolean {
        return mShowText
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mShowText) {
            if (mOnLayout == null) {
                mOnLayout = makeLayout(mTextOn!!, mTextOnPaint)
            }
            if (mOffLayout == null) {
                mOffLayout = makeLayout(mTextOff!!, mTextOffPaint)
            }
        }
        val padding = mTempRect
        val thumbWidth: Int
        val thumbHeight: Int
        if (mThumbDrawable != null) {
            // Cached thumb width does not include padding.
            mThumbDrawable!!.getPadding(padding)
            thumbWidth = mThumbDrawable!!.intrinsicWidth - padding.left - padding.right
            thumbHeight = mThumbDrawable!!.intrinsicHeight
        } else {
            thumbWidth = 0
            thumbHeight = 0
        }

        val trackWidth: Int
        val trackHeight: Int
        if (mTrackDrawable != null) {
            // Cached thumb width does not include padding.
            mTrackDrawable!!.getPadding(padding)
            trackWidth = mTrackDrawable!!.intrinsicWidth - padding.left - padding.right
            trackHeight = mTrackDrawable!!.intrinsicHeight
        } else {
            padding.setEmpty()
            trackWidth = 0
            trackHeight = 0
        }

        mThumbWidth = thumbWidth

        mTextMaxWidth = if (mShowText) {
            (mOnLayout!!.width.coerceAtLeast(mOffLayout!!.width)
                    + mThumbTextPadding * 2)
        } else {
            0
        }
        // Adjust left and right padding to ensure there's enough room for the
        // thumb's padding (when present).
        var paddingLeft = padding.left
        var paddingRight = padding.right
        if (mThumbDrawable != null) {
            val inset = mThumbDrawable!!.opticalInsets
            paddingLeft = paddingLeft.coerceAtLeast(inset.left)
            paddingRight = paddingRight.coerceAtLeast(inset.right)
        }
//        val switchWidth = Math.max(
//            mSwitchMinWidth,
//            2 * mTextMaxWidth + paddingLeft + paddingRight
//        )

        val switchWidth = Math.max(
            mSwitchMinWidth,
            mThumbWidth + paddingLeft + paddingRight
        )
        mThumbWidth = switchWidth - (paddingLeft + paddingRight)
        //val switchHeight = Math.max(trackHeight, thumbHeight)
        mSwitchWidth = switchWidth
        //mSwitchHeight = measuredHeight// switchHeight
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //val measuredHeight = measuredHeight
        mSwitchHeight = measuredHeight// switchHeight
//        if (measuredHeight < switchHeight) {
//            setMeasuredDimension(measuredWidthAndState, switchHeight)
//        }
    }

    private fun makeLayout(text: CharSequence, textPaint: TextPaint): Layout {
        val width = ceil(Layout.getDesiredWidth(text, textPaint)).toInt()

        return if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
            StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                .setUseLineSpacingFromFallbacks(mUseFallbackLineSpacing)
                .build()
        } else {
            StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                .build()
        }
    }

    private fun hitThumb(x: Float, y: Float): Boolean {
        if (mThumbDrawable == null) {
            return false
        }

        // Relies on mTempRect, MUST be called first!
        val thumbOffset = getThumbOffset()
        mThumbDrawable!!.getPadding(mTempRect)
        val thumbTop = mSwitchTop + thumbOffset - mTouchSlop
        val thumbLeft = mSwitchLeft - mTouchSlop
        val thumbRight = thumbLeft + mThumbWidth +
                mTempRect.left + mTempRect.right + mTouchSlop
        val thumbBottom = mSwitchBottom + mTouchSlop
        return x > thumbLeft && x < thumbRight && y > thumbTop && y < thumbBottom
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mVelocityTracker.addMovement(ev)
        val action = ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                if (isEnabled && hitThumb(x, y)) {
                    mTouchMode = TOUCH_MODE_DOWN
                    mTouchX = x
                    mTouchY = y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (mTouchMode) {
                    TOUCH_MODE_IDLE -> {}
                    TOUCH_MODE_DOWN -> {
                        val x = ev.x
                        val y = ev.y
                        if (abs(x - mTouchX) > mTouchSlop ||
                            abs(y - mTouchY) > mTouchSlop
                        ) {
                            mTouchMode = TOUCH_MODE_DRAGGING
                            parent.requestDisallowInterceptTouchEvent(true)
                            mTouchX = x
                            mTouchY = y
                            return true
                        }
                    }
                    TOUCH_MODE_DRAGGING -> {
                        val y = ev.y
                        val thumbScrollRange = getThumbScrollRange()
                        val thumbScrollOffset = y - mTouchY
                        var dPos: Float
                        dPos = if (thumbScrollRange != 0) {
                            thumbScrollOffset / thumbScrollRange
                        } else {
                            // If the thumb scroll range is empty, just use the
                            // movement direction to snap on or off.
                            if (thumbScrollOffset > 0) 1f else -1f
                        }
                        if (isLayoutRtl()) {
                            dPos = -dPos
                        }
                        val newPos: Float = constrain(mThumbPosition + dPos, 0f, 1f)
                        if (newPos != mThumbPosition) {
                            mTouchY = y
                            setThumbPosition(newPos)
                        }
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    stopDrag(ev)
                    // Allow super class to handle pressed state, etc.
                    super.onTouchEvent(ev)
                    return true
                }
                mTouchMode = TOUCH_MODE_IDLE
                mVelocityTracker.clear()
            }
        }
        val rtVal = super.onTouchEvent(ev)
        return rtVal
    }

    private fun cancelSuperTouch(ev: MotionEvent) {
        val cancel = MotionEvent.obtain(ev)
        cancel.action = MotionEvent.ACTION_CANCEL
        super.onTouchEvent(cancel)
        cancel.recycle()
    }

    private fun stopDrag(ev: MotionEvent) {
        mTouchMode = TOUCH_MODE_IDLE

        // Commit the change if the event is up and not canceled and the switch
        // has not been disabled during the drag.
        val commitChange = ev.action == MotionEvent.ACTION_UP && isEnabled
        val oldState = isChecked
        val newState: Boolean = if (commitChange) {
            mVelocityTracker.computeCurrentVelocity(1000)
            val xvel = mVelocityTracker.xVelocity
            if (abs(xvel) > mMinFlingVelocity) {
                if (isLayoutRtl()) xvel < 0 else xvel > 0
            } else {
                getTargetCheckedState()
            }
        } else {
            oldState
        }
        if (newState != oldState) {
            playSoundEffect(SoundEffectConstants.CLICK)
        }
        // Always call setChecked so that the thumb is moved back to the correct edge
        isChecked = newState
        cancelSuperTouch(ev)
    }

    private fun animateThumbToCheckedState(newCheckedState: Boolean) {
        val targetPosition: Float = if (newCheckedState) 1f else 0f
        mPositionAnimator = ObjectAnimator.ofFloat(this, THUMB_POS, targetPosition)
        mPositionAnimator!!.duration = THUMB_ANIMATION_DURATION.toLong()
        mPositionAnimator!!.setAutoCancel(true)
        mPositionAnimator!!.start()
    }

    private fun cancelPositionAnimator() {
        mPositionAnimator?.cancel()
    }

    private fun getTargetCheckedState(): Boolean {
        return mThumbPosition > 0.5f
    }

    private fun setThumbPosition(position: Float) {
        mThumbPosition = position
        invalidate()
    }

    override fun toggle() {
        isChecked = !isChecked
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    protected fun setDefaultStateDescription(){
        if (mTextOn == null)
            mTextOn = "ON"
        if (mTextOff == null)
            mTextOff = "OFF"
    }
    protected fun getButtonStateDescription(): CharSequence {
        return if (isChecked) {
//            if (mTextOn == null) resources.getString(R.string.capital_on) else mTextOn!!
            if (mTextOn == null) "ON" else mTextOn!!
        } else {
//            if (mTextOff == null) resources.getString(R.string.capital_off) else mTextOff!!
            if (mTextOff == null) "OFF" else mTextOff!!
        }
    }

    override fun setChecked(checked: Boolean) {
        var checkedTemp = checked
        super.setChecked(checked)

        // Calling the super method may result in setChecked() getting called
        // recursively with a different value, so load the REAL value...
        checkedTemp = isChecked
        if (isAttachedToWindow && isLaidOut) {
            animateThumbToCheckedState(checkedTemp)
        } else {
            // Immediately move the thumb to the new position.
            cancelPositionAnimator()
            setThumbPosition(if (checkedTemp) 1f else 0f)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var opticalInsetLeft = 0
        var opticalInsetRight = 0
        if (mThumbDrawable != null) {
            val trackPadding = mTempRect
            if (mTrackDrawable != null) {
                mTrackDrawable!!.getPadding(trackPadding)
            } else {
                trackPadding.setEmpty()
            }
            val insets = mThumbDrawable!!.opticalInsets
            opticalInsetLeft = 0.coerceAtLeast(insets.left - trackPadding.left)
            opticalInsetRight = 0.coerceAtLeast(insets.right - trackPadding.right)
        }
        val switchRight: Int
        val switchLeft: Int
        if (isLayoutRtl()) {
            switchLeft = paddingLeft + opticalInsetLeft
            switchRight = switchLeft + mSwitchWidth - opticalInsetLeft - opticalInsetRight
        } else {
            switchRight = width - paddingRight - opticalInsetRight
            switchLeft = switchRight - mSwitchWidth + opticalInsetLeft + opticalInsetRight
        }
        val switchTop: Int
        val switchBottom: Int
        when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.TOP -> {
                switchTop = paddingTop
                switchBottom = switchTop + mSwitchHeight
            }
            Gravity.CENTER_VERTICAL -> {
                switchTop = (paddingTop + height - paddingBottom) / 2 -
                        mSwitchHeight / 2
                switchBottom = switchTop + mSwitchHeight
            }
            Gravity.BOTTOM -> {
                switchBottom = height - paddingBottom
                switchTop = switchBottom - mSwitchHeight
            }
            else -> {
                switchTop = paddingTop
                switchBottom = switchTop + mSwitchHeight
            }
        }
        mSwitchLeft = switchLeft
        mSwitchTop = switchTop
        mSwitchBottom = switchBottom
        mSwitchRight = switchRight
    }

    override fun draw(c: Canvas?) {
        val padding = mTempRect
        val switchLeft = mSwitchLeft
        val switchTop = mSwitchTop
        val switchRight = mSwitchRight
        val switchBottom = mSwitchBottom
        var thumbInitialTop = switchTop + getThumbOffset()
        val thumbInsets: Insets = if (mThumbDrawable != null) {
            mThumbDrawable!!.opticalInsets
        } else {
            Insets.NONE
        }

        // Layout the track.
        var trackTop = switchTop
        var trackBottom = switchBottom
        if (mTrackDrawable != null) {
            mTrackDrawable!!.getPadding(padding)

            // Adjust thumb position for track padding.
            thumbInitialTop += padding.top

            // If necessary, offset by the optical insets of the thumb asset.
            var trackLeft = switchLeft
            var trackRight = switchRight
            if (thumbInsets !== Insets.NONE) {
                if (thumbInsets.left > padding.left) {
                    trackLeft += thumbInsets.left - padding.left
                }
                if (thumbInsets.top > padding.top) {
                    trackTop += thumbInsets.top - padding.top
                }
                if (thumbInsets.right > padding.right) {
                    trackRight -= thumbInsets.right - padding.right
                }
                if (thumbInsets.bottom > padding.bottom) {
                    trackBottom -= thumbInsets.bottom - padding.bottom
                }
            }
            mTrackDrawable!!.setBounds(trackLeft, trackTop + 40, trackRight, trackBottom - 40)
        }


        // Layout the thumb.
        if (mThumbDrawable != null) {
            mThumbDrawable!!.getPadding(padding)
            val thumbTop = if (thumbInitialTop == 0) 40 else thumbInitialTop - 40
            val thumbBottom = thumbTop + mThumbWidth
            val thumbLeft = switchLeft - padding.left
            val thumbRight = switchLeft + mThumbWidth + padding.right

            mThumbDrawable?.setBounds(thumbLeft, thumbTop, thumbRight, thumbBottom)
            val background = background
            background?.setHotspotBounds(thumbLeft, switchTop, thumbRight, switchBottom)
        }
        // Draw the background.
        super.draw(c)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = mTempRect
        val switchTop = mSwitchTop
        val switchBottom = mSwitchBottom
        val switchInnerTop = switchTop + padding.top
        val switchInnerBottom = switchBottom - padding.bottom
        mTrackDrawable?.getPadding(padding) ?: padding.setEmpty()
        mTrackDrawable?.draw(canvas)
        val saveCount = canvas.save()
        mThumbDrawable?.draw(canvas)

        val switchText = if (getTargetCheckedState()) mOnLayout else mOffLayout
        if (switchText != null && mShowText) {
            val textColors = if (getTargetCheckedState()) mTextOnColors else mTextOffColors
            val textPaint = if (getTargetCheckedState()) mTextOnPaint else mTextOffPaint
            if (textColors != null) {
                textPaint.color = textColors.getColorForState(drawableState, 0)
            }
            val circlePadding = (height - paddingLeft - paddingEnd)/4f
            if (getTargetCheckedState()){
                mTextBoundLeft += circlePadding
            }else{
                mTextBoundRight -= circlePadding
            }
            textPaint.drawableState = drawableState
            textPaint.textAlign = Paint.Align.CENTER
            val cX = mTextBoundLeft + mTextBoundRight

            val left = cX / 2f
            val top = (switchInnerTop + switchInnerBottom) / 2f - switchText.height / 2f
//            canvas.translate(left, top)
            canvas.translate(top, left)
            switchText.draw(canvas)

        }
        canvas.restoreToCount(saveCount)
    }

    override fun getCompoundPaddingLeft(): Int {
        if (!isLayoutRtl()) {
            return super.getCompoundPaddingLeft()
        }
        var padding = super.getCompoundPaddingLeft() + mSwitchWidth
        if (!TextUtils.isEmpty(text)) {
            padding += mSwitchPadding
        }
        return padding
    }

    override fun getCompoundPaddingRight(): Int {
        if (isLayoutRtl()) {
            return super.getCompoundPaddingRight()
        }
        var padding = super.getCompoundPaddingRight() + mSwitchWidth
        if (!TextUtils.isEmpty(text)) {
            padding += mSwitchPadding
        }
        return padding
    }

    private fun getThumbOffset(): Int {
        val thumbPosition: Float = if (isLayoutRtl()) {
            1 - mThumbPosition
        } else {
            mThumbPosition
        }
        return (thumbPosition * getThumbScrollRange() + 0.5f).toInt()
    }

    private fun getThumbScrollRange(): Int {
        return if (mTrackDrawable != null) {
            val padding = mTempRect
            mTrackDrawable!!.getPadding(padding)
            val insets: Insets = if (mThumbDrawable != null) {
                mThumbDrawable!!.opticalInsets
            } else {
                Insets.NONE
            }
            (mSwitchHeight - mThumbWidth - padding.bottom - padding.top
                    - insets.top - insets.bottom)
        } else {
            0
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        return super.onCreateDrawableState(extraSpace + 1)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val state = drawableState
        var changed = false
        val thumbDrawable = mThumbDrawable
        if (thumbDrawable != null && thumbDrawable.isStateful) {
            changed = changed or thumbDrawable.setState(state)
        }
        val trackDrawable = mTrackDrawable
        if (trackDrawable != null && trackDrawable.isStateful) {
            changed = changed or trackDrawable.setState(state)
        }
        if (changed) {
            invalidate()
        }
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (mThumbDrawable != null) {
            mThumbDrawable!!.setHotspot(x, y)
        }
        if (mTrackDrawable != null) {
            mTrackDrawable!!.setHotspot(x, y)
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mThumbDrawable || who === mTrackDrawable
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (mThumbDrawable != null) {
            mThumbDrawable!!.jumpToCurrentState()
        }
        if (mTrackDrawable != null) {
            mTrackDrawable!!.jumpToCurrentState()
        }
        if (mPositionAnimator != null && mPositionAnimator?.isStarted == true) {
            mPositionAnimator?.end()
            mPositionAnimator = null
        }
    }

    override fun getAccessibilityClassName(): CharSequence? {
        return Switch::class.java.name
    }


    protected fun onProvideStructure(structure: ViewStructure, viewFor: Int, flags: Int) {}

    private val THUMB_POS: FloatProperty<XSwitchVertical> = object : FloatProperty<XSwitchVertical>("thumbPos") {
        override fun get(obj: XSwitchVertical): Float {
            return obj.mThumbPosition
        }

        override fun setValue(obj: XSwitchVertical, value: Float) {
            obj.setThumbPosition(value)
        }
    }

    private fun isLayoutRtl(): Boolean {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    fun constrain(amount: Int, low: Int, high: Int): Int {
        return if (amount < low) low else if (amount > high) high else amount
    }

    fun constrain(amount: Float, low: Float, high: Float): Float {
        return if (amount < low) low else if (amount > high) high else amount
    }
}