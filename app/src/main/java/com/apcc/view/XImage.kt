package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.view.GestureDetector
import androidx.appcompat.widget.AppCompatImageView
import com.apcc.emma.R
import com.apcc.utils.Util
import com.apcc.utils.ViewHelper
import com.apcc.view.image.*


/**
 * using for show image detail
 * Image can zoom
 */
@SuppressLint("AppCompatCustomView")
class XImage : AppCompatImageView, ViewInterface {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }

    private var attacher: PhotoViewAttacher? = null
    private var pendingScaleType: ScaleType? = null


    private var mRadius = 0.0f
    private var mPath:Path? = Path()
    private var mImagePath:String? = ""
    private var mImageDefault:Int = 0
    private var mAllowCache = false
    private var mTint: ColorStateList? = null

    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////
    override val mStyleableID: IntArray = R.styleable.XImage

    override fun extraTypes(typedArray: TypedArray) {
        mRadius = typedArray.getDimension(R.styleable.XImage_cornerRadius, 0f)
        mImagePath = typedArray.getString(R.styleable.XImage_imagePath)
        mImageDefault = typedArray.getResourceId(R.styleable.XImage_imageDefault,0)
        mAllowCache = typedArray.getBoolean(R.styleable.XImage_allowCache,false)
        mTint = typedArray.getColorStateList(R.styleable.XImage_android_tint)
    }

    override fun parseAttributes(context: Context?, attrs: AttributeSet?) {
        attacher = PhotoViewAttacher(this)
        //We always pose as a Matrix scale type, though we can change to another scale type via the attacher
        super.setScaleType(ScaleType.MATRIX)
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType!!)
            pendingScaleType = null
        }
        super.parseAttributes(context, attrs)
    }

    override fun updateView() {
        setScaleLevels(1f, 3f, 10f)
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
            ViewHelper.loadImage(path, mImageDefault, mAllowCache)?.into(this@XImage)?:setImageDefault(mImageDefault)
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

    fun getAttacher(): PhotoViewAttacher? {
        return attacher
    }

    override fun getScaleType(): ScaleType? {
        return attacher?.scaleType
    }

    override fun getImageMatrix(): Matrix? {
        return attacher?.imageMatrix
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        attacher?.setOnLongClickListener(l)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        attacher?.setOnClickListener(l)
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType
        } else {
            attacher?.scaleType = scaleType
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher?.update()
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (attacher != null) {
            attacher?.update()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        if (attacher != null) {
            attacher?.update()
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) {
            attacher?.update()
        }
        return changed
    }

    fun setRotationTo(rotationDegree: Float) {
        attacher?.setRotationTo(rotationDegree)
    }

    fun setRotationBy(rotationDegree: Float) {
        attacher?.setRotationBy(rotationDegree)
    }

    fun isZoomable(): Boolean {
        return attacher?.isZoomable?:false
    }

    fun setZoomable(zoomable: Boolean) {
        attacher?.isZoomable = zoomable
    }

    fun getDisplayRect(): RectF? {
        return attacher?.displayRect
    }

    fun getDisplayMatrix(matrix: Matrix?) {
        attacher?.getDisplayMatrix(matrix)
    }

    fun setDisplayMatrix(finalRectangle: Matrix?): Boolean {
        return attacher?.setDisplayMatrix(finalRectangle)?:false
    }

    fun getSuppMatrix(matrix: Matrix?) {
        attacher?.getSuppMatrix(matrix)
    }

    fun setSuppMatrix(matrix: Matrix?): Boolean {
        return attacher?.setDisplayMatrix(matrix)?:false
    }

    fun getMinimumScale(): Float {
        return attacher?.minimumScale?:1f
    }

    fun getMediumScale(): Float {
        return attacher?.mediumScale?:1f
    }

    fun getMaximumScale(): Float {
        return attacher?.maximumScale?:1f
    }

    fun getScale(): Float {
        return attacher?.scale?:1f
    }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        attacher?.setAllowParentInterceptOnEdge(allow)
    }

    fun setMinimumScale(minimumScale: Float) {
        attacher?.minimumScale = minimumScale
    }

    fun setMediumScale(mediumScale: Float) {
        attacher?.mediumScale = mediumScale
    }

    fun setMaximumScale(maximumScale: Float) {
        attacher?.maximumScale = maximumScale
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        attacher?.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener?) {
        attacher?.setOnMatrixChangeListener(listener)
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener?) {
        attacher?.setOnPhotoTapListener(listener)
    }

    fun setOnOutsidePhotoTapListener(listener: OnOutsidePhotoTapListener?) {
        attacher?.setOnOutsidePhotoTapListener(listener)
    }

    fun setOnViewTapListener(listener: OnViewTapListener?) {
        attacher?.setOnViewTapListener(listener)
    }

    fun setOnViewDragListener(listener: OnViewDragListener?) {
        attacher?.setOnViewDragListener(listener)
    }

    fun setScale(scale: Float) {
        attacher?.scale = scale
    }

    fun setScale(scale: Float, animate: Boolean) {
        attacher?.setScale(scale, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        attacher?.setScale(scale, focalX, focalY, animate)
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        attacher?.setZoomTransitionDuration(milliseconds)
    }

    fun setOnDoubleTapListener(onDoubleTapListener: GestureDetector.OnDoubleTapListener?) {
        attacher?.setOnDoubleTapListener(onDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangedListener: OnScaleChangedListener?) {
        attacher?.setOnScaleChangeListener(onScaleChangedListener)
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener?) {
        attacher?.setOnSingleFlingListener(onSingleFlingListener)
    }

}