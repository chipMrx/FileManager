package com.apcc.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.media.Image
import android.util.Log
import android.view.WindowManager


object BitmapUtils {

    fun decodeBitmap(image:Image?):Bitmap?{
        if (image == null)
            return null
        var bitmapImage:Bitmap? = null
        try {
            val buffer = image.planes[0].buffer
            //val bytes = ByteArray(buffer.remaining())
            val bytes = ByteArray(buffer.capacity())
            buffer[bytes]
            bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            image.close()
        }
        return bitmapImage
    }

    fun bitmapToMatrixColor(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val batchNum = 0
        val input = Array(1) { Array(bitmap.height) { Array(bitmap.width) { FloatArray(3) } } }
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                input[batchNum][y][x][0] = normalColor(Color.red(pixel))
                input[batchNum][y][x][1] = normalColor(Color.green(pixel))
                input[batchNum][y][x][2] = normalColor(Color.blue(pixel))
            }
        }
        return input
    }

    private fun normalColor(color:Int):Float{
        return color*2/255f-1
    }

    fun formatBitmapIn(bitmap: Bitmap?, scaleW:Int, scaleH:Int): Bitmap? {
        bitmap?.let {
            val bm = Bitmap.createScaledBitmap(it, scaleW, scaleH, false)
            return bm.copy(Bitmap.Config.ARGB_8888, true)
        }
        return null
    }

    fun subImage(originBitmap: Bitmap?, x: Int, y: Int, w: Int, h: Int): Bitmap? {
        if(originBitmap == null
            || w <= 0 || h <= 0) { // width and height must be >0
            return null
        }
        var startX = x
        var startY = y
        var width = w
        var height = h
        // valid for sub
        if ( startX < 0)
            startX = 0
        if ( startY < 0)
            startY = 0
        if (width > originBitmap.width - startX)
            width = originBitmap.width - startX
        if (height > originBitmap.height - startY)
            height = originBitmap.height - startY
        // check valid width, height
        if(width <= 0 || height <= 0) {
            return null
        }

        return Bitmap.createBitmap(originBitmap, startX, startY, width, height)
    }

    /**
     * @param rotate: degrees values : 0, 90, 180, 270
     */
    fun rotateImage(src:Bitmap, rotate:Float):Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotate)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    fun bitmapFromResource(context:Context, resourceImage:Int):Bitmap?{
        return BitmapFactory.decodeResource(context.resources, resourceImage)
    }

    fun drawResizedBitmap(src: Bitmap, dst: Bitmap) {
        val minDim = src.width.coerceAtMost(src.height).toFloat()
        val matrix = Matrix()

        // We only want the center square out of the original rectangle.
        val translateX = -(0f.coerceAtLeast((src.width - minDim) / 2f))
        val translateY = -(0f.coerceAtLeast((src.height - minDim) / 2f))
        matrix.preTranslate(translateX, translateY)
        val scaleFactor = dst.height / minDim
        matrix.postScale(scaleFactor, scaleFactor)

        val canvas = Canvas(dst)
        canvas.drawBitmap(src, matrix, null)
    }

    //////////////////////////////////////////////////////////////////////////////
    /// native code
    //////////////////////////////////////////////////////////////////////////////
    external fun convertYUV420SPToARGB8888(
        input: ByteArray?,
        output: IntArray?,
        width: Int,
        height: Int,
        halfSize: Boolean
    )

    external fun convertYUV420ToARGB8888(
        y: ByteArray?,
        u: ByteArray?,
        v: ByteArray?,
        output: IntArray?,
        width: Int,
        height: Int,
        yRowStride: Int,
        uvRowStride: Int,
        uvPixelStride: Int,
        halfSize: Boolean
    )

    external fun convertYUV420SPToRGB565(
        input: ByteArray?, output: ByteArray?, width: Int, height: Int
    )
    external fun convertARGB8888ToYUV420SP(
        input: IntArray?, output: ByteArray?, width: Int, height: Int
    )
    external fun convertRGB565ToYUV420SP(
        input: ByteArray?, output: ByteArray?, width: Int, height: Int
    )
}