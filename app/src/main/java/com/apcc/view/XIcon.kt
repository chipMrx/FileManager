package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.apcc.emma.R
import com.apcc.utils.Util
import com.apcc.utils.ViewHelper

/**
 * just show image only
 */
@SuppressLint("AppCompatCustomView")
class XIcon : ImageView, ViewInterface {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes){
        parseAttributes(context, attrs)
    }

    private var mRadius = 0.0f
    private var mPath:Path? = Path()
    private var mImagePath:String? = ""
    private var mImageDefault:Int = 0
    private var mAllowCache = false
    private var mTint: ColorStateList? = null

    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////
    override val mStyleableID: IntArray = R.styleable.XIcon

    override fun extraTypes(typedArray: TypedArray) {
        mRadius = typedArray.getDimension(R.styleable.XIcon_cornerRadius, 0f)
        mImagePath = typedArray.getString(R.styleable.XIcon_imagePath)
        mImageDefault = typedArray.getResourceId(R.styleable.XIcon_imageDefault,0)
        mAllowCache = typedArray.getBoolean(R.styleable.XIcon_allowCache,false)
        mTint = typedArray.getColorStateList(R.styleable.XIcon_android_tint)
    }

    override fun updateView() {
        loadByPath(mImagePath)
        imageTintList = mTint
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (mPath != null && mRadius >0){
            val rect = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())
            mPath!!.addRoundRect(rect, mRadius, mRadius, Path.Direction.CW)
            canvas.clipPath(mPath!!)
        }
        super.onDraw(canvas)
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////
    /**
     * default image: R.drawable.ic_img
     */
    fun loadByPath(path:String?, defaultImg:Int = 0){
        mImagePath = path
        if(defaultImg != 0){
            mImageDefault = defaultImg
        }
        if (TextUtils.isEmpty(path)){
            if(mImageDefault > 0){
                setImageResource(mImageDefault)
            }
        }else{
            ViewHelper.loadImage(path, mImageDefault, mAllowCache)?.apply {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    this.resize(measuredWidth, measuredHeight)
                    this.onlyScaleDown()
                    this.centerCrop()
                }
                this.into(this@XIcon)
            }?:setImageDefault(mImageDefault)
        }
    }

    fun setImageDefault(defaultImg:Int){
        mImageDefault = defaultImg
        if (TextUtils.isEmpty(mImagePath)
            && mImageDefault > 0){
            setImageResource(mImageDefault)
        }
    }

    /**
     * using float as pixel
     */
    fun setCornerRadius(radius:Float){
        mRadius = radius
    }

    /**
     * using dimen to set
     */
    fun setCornerRadius(radiusSource:Int){
        context?.let {
            mRadius = Util.getDimension(context, radiusSource)
        }
    }

    fun setTintColor(resource:Int, isVector:Boolean = false){
        if (resource == 0){
            clearColorFilter()
            return
        }
        if (isVector)
            setColorFilter(ContextCompat.getColor(context, resource), android.graphics.PorterDuff.Mode.SRC_IN)
        else
            setColorFilter(ContextCompat.getColor(context, resource), android.graphics.PorterDuff.Mode.MULTIPLY)
    }


}