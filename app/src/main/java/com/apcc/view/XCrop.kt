package com.apcc.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.apcc.emma.R
import com.apcc.data.AppPoint
import kotlin.math.*

class XCrop : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var mListener:DrawingListener?=null

    /**
     * 4 points
     */
    var mListPoint:MutableList<AppPoint> = ArrayList()

    private var lastAction:Long = 0
    private var lastInvalidate:Long = 0

    private val mPaintCropLine:Paint
    private val mPaintCropPoint:Paint

    var mRect: RectF? = null
    private var marginW:Float = 0f
    private var marginH:Float = 0f
    private var mImageW:Int = 0
    private var mImageH:Int = 0

    private var ratio:Float = 1f

    private var cropPointTargeted: AppPoint? = null

    init {
        mPaintCropLine = Paint().apply {
            color = ContextCompat.getColor(context, R.color.colorNormalBorder)
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        mPaintCropPoint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.white)
            strokeWidth = 5f
//            textAlign = Paint.Align.CENTER
            style = Paint.Style.STROKE
        }

        setBackgroundColor(Color.TRANSPARENT)

        setOnTouchListener { _, event ->
            if (validTimeAction(lastAction, 1000 / FRESH_SCREEN_FPS.toLong())) {
                var hasMove = false
                lastAction = System.currentTimeMillis()
                event?.let { ev ->
                    when (ev.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                            targetCropPoint(ev.x, ev.y)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (moveFocusPoint(ev.x, ev.y)) {
                                hasMove = true
                            }
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                            moveFocusPoint(ev.x, ev.y)
                            //lastAction = System.currentTimeMillis()
                            cleanSelectedPoint()
                        }
                    }
                    if(hasMove){
                        invalidate()
                    }

                }
            }
            true
        }
        setOnClickListener {
            // convert from view location to bitmap location
            //mListener?.onBitmapClick((startX - marginW) / ratio, (startY - marginH)/ratio)
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSize()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {cv->
            if (mListPoint.isEmpty())
                return
            // draw line
            var firstPoint: AppPoint? = null
            var currentPoint: AppPoint? = null
            for (nextPoint in mListPoint){
                if (firstPoint == null){
                    firstPoint = nextPoint
                }
                drawLine(cv, currentPoint, nextPoint)
                currentPoint = nextPoint
            }
            // draw last line
            drawLine(cv, currentPoint, firstPoint)
        }
    }

    override fun invalidate() {
        val freshTime:Long = 1000/ FRESH_SCREEN_FPS.toLong()
        if (validTimeAction( lastInvalidate , freshTime)) {
            lastInvalidate = System.currentTimeMillis()
            super.invalidate()
        }
    }

    //////////////////////////
    /// public fun
    //////////////////////////
    fun initInfo(imageW:Int = width, imageH:Int = height){

        updateViewSize()
    }

    fun setOnDrawingListener(listener:DrawingListener){
        mListener = listener
    }

    fun selectAll(){
        //mListCirclePoint.clear()
        initSelectedArea()
        invalidate()
    }


    //////////////////////////
    // private fun
    //////////////

    private fun drawLine(canvas: Canvas?, startPoint: AppPoint?, endPoint: AppPoint?){
        canvas?.let { cv->
            startPoint?.let { stPoint->
                endPoint?.let { edPoint->
                    // line border
                    cv.drawLine(stPoint.x, stPoint.y, edPoint.x, edPoint.y, mPaintCropLine)
                    // point start
                    val highlightPointStart = calculateHighlightPoint(stPoint, edPoint)
                    cv.drawLine(stPoint.x, stPoint.y, highlightPointStart.x, highlightPointStart.y, mPaintCropPoint)
                    // point end
                    val highlightPointEnd = calculateHighlightPoint(edPoint, stPoint)
                    cv.drawLine(highlightPointEnd.x, highlightPointEnd.y, edPoint.x, edPoint.y, mPaintCropPoint)
                }
            }
        }
    }

    private fun validTimeAction( lastTime:Long, minTime:Long = 300 ):Boolean{
        return System.currentTimeMillis() - lastTime >= minTime
    }

    private fun validX(x:Float):Float{
        return when {
            x < marginW -> marginW
            x > width-marginW -> width-marginW
            else -> x
        }
    }

    private fun validY(y:Float):Float{
        return when {
            y < marginH -> marginH
            y > height-marginH -> height-marginH
            else -> y
        }
    }

    private fun initSelectedArea(){

    }

    private fun cleanSelectedPoint(){
        cropPointTargeted = null
    }

    private fun updateSelectedArea(){
//        left = min(startX, endX)
//        top = min(startY, endY)
//        right = max(startX, endX)
//        bottom = max(startY, endY)
    }

    private fun targetCropPoint(x:Float, y:Float){
        if (mListPoint.size == 4) {
            for (point in mListPoint) {
                if (isInsideCircle(validX(x) , validY(y), point)){
                    cropPointTargeted = point
                    return
                }
            }
        }
        ///
        cleanSelectedPoint()
        invalidate()// not to redraw view
    }

    /**
     * when user swipe a distance > #MIN_SWIPE_PIXELS
     * return true : is moved
     */
    private fun moveFocusPoint(x:Float, y:Float):Boolean{
        cropPointTargeted?.let {
            val isMoved = sqrt( (x - it.x).pow(2) + (y - it.y).pow(2)) > MIN_SWIPE_PIXELS
            if (isMoved){
                it.set(validX(x), validY(y))
            }else{// clear selected box
                //cleanSelectedPoint()
            }
            return isMoved
        }
        return false
    }

    private fun updateViewSize(){
        //reset drawing
        initSelectedArea()
        mPaintCropPoint.strokeWidth = ((width + height) / 400f).coerceAtLeast(5f)
        ratio = min(division(width , mImageW), division(height, mImageH))
        marginW = (width - mImageW*ratio)/2
        marginH = (height - mImageH*ratio)/2

        mImageW = width
        mImageH = height
        mListPoint = ArrayList()
        mListPoint.add(AppPoint(366,378))
        mListPoint.add(AppPoint(394, 779))
        mListPoint.add(AppPoint(732, 930))
        mListPoint.add(AppPoint(789, 195))
        invalidate()
    }

    /**
     * return real rect for image
     */
    fun getRealRect(): Rect {
        val rect = Rect()
        rect.set(((left-marginW)/ratio).toInt(),
            ((top-marginH)/ratio).toInt(),
            ((right - marginW)/ratio).toInt(),
            ((bottom-marginH)/ratio).toInt())
        return rect
    }

    private fun division(a:Int, b:Int):Float{
        return  division(a.toFloat(),b.toFloat())
    }
    private fun division(a:Float, b:Float):Float{
        return a/b
    }

    //////////////////////////////
    private fun isInsideCircle(x:Float, y:Float, point: AppPoint):Boolean{
        val midDistance = computeDistance(point.x, point.y, x, y)
        return midDistance < TOUCH_OUTSIDE_POINT
    }

    private fun isSamePoint(point1: AppPoint, point2: AppPoint):Boolean{
        return isSamePoint(point1.x, point1.y, point2.x, point2.y)
    }

    private fun isSamePoint(point: AppPoint, x:Float, y:Float):Boolean{
        return isSamePoint(x, y, point.x, point.y)
    }

    private fun isSamePoint(x1: Float, y1: Float, x2: Float, y2: Float):Boolean{
        return sameFloatNumber(x1, x2) && sameFloatNumber(y1, y2)
    }

    private fun sameFloatNumber(a:Float, b:Float):Boolean{

        return abs(a-b) < 0.0001
    }

    private fun computeDistance(startPoint: AppPoint, endPoint: AppPoint):Float{
        return computeDistance(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
    }
    private fun computeDistance(x1:Float, y1:Float,x2:Float, y2:Float):Float{
        val x = x2-x1
        val y = y2-y1
        return sqrt( x.pow(2) + y.pow(2))
    }


    /**
     * linear equations: (y1−y2)(x−x1)+(x2−x1)(y−y1)=0
    => xy1 - xy2 - x1y1 + x1y2 + x2y - x2y1 - x1y + x1y1 = 0
    => x(y1 - y2) + x1y2 + x2y - x2y1 - x1y = 0
    => x(y1 - y2) + y(x2 - x1) + x1y2 - x2y1 = 0
    => x = (x2y1 - y(x2 - x1) - x1y2) / (y1 - y2)
    --------------------------------------------
    find Y: distance between points
    sqrt( (x-x1)^2 + (y-y1)^2 ) = distance
    => (x-x1)^2 + (y-y1)^2 = distance^2
    => (y-y1)^2 = distance^2 - (x-x1)^2
    => y = sqrt(distance^2 - (x-x1)^2) - y1
    ________________________________________________________________
    x = (x2y1 - (sqrt(distance^2 - (x-x1)^2) - y1)(x2 - x1) - x1y2) / (y1 - y2)
     */
    private fun calculateHighlightPoint(stP: AppPoint, edP: AppPoint): AppPoint {
        val distance = computeDistance(stP, edP)
        if (distance < MIN_POINT_LINE_LENGTH)
            return edP
        val dP = stP.x - edP.x
        val kP = stP.y - edP.y
        val dM = dP * MIN_POINT_LINE_LENGTH / distance
        val kM = kP * MIN_POINT_LINE_LENGTH / distance

        val x = stP.x - dM
        val y = stP.y - kM
        return AppPoint(x, y)
    }

    companion object{
        private const val MIN_SWIPE_PIXELS = 1f
        private const val MIN_POINT_LINE_LENGTH = 50f // pixel
        private const val FRESH_SCREEN_FPS = 60
        private const val TOUCH_OUTSIDE_POINT = 60f


    }

    interface DrawingListener{
        fun onBitmapClick( x:Float, y:Float )
    }

}