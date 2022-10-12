package com.apcc.utils

import android.app.Activity
import android.hardware.camera2.CaptureRequest
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import com.apcc.emma.R
import com.apcc.comparator.CompareSizesByArea
import java.util.*

object CameraUtil {


    /**
     * return array of orientation as degree
     */
    fun getOrientationsArray():SparseIntArray{
        val orientations = SparseIntArray()
        orientations.append(Surface.ROTATION_0, getOrientation(Surface.ROTATION_0))
        orientations.append(Surface.ROTATION_90, getOrientation(Surface.ROTATION_90))
        orientations.append(Surface.ROTATION_180, getOrientation(Surface.ROTATION_180))
        orientations.append(Surface.ROTATION_270, getOrientation(Surface.ROTATION_270))
        return orientations
    }

    /**
     * return orientation as degree
     */
    fun getOrientation(rotation:Int):Int{
        return when(rotation){
            Surface.ROTATION_0 -> 90
            Surface.ROTATION_90 -> 0
            Surface.ROTATION_180 -> 270
            Surface.ROTATION_270 -> 180
            else -> 0
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    fun getOrientation(activity:Activity?, sensorOrientation:Int): Int {
        if (activity == null) return 0
        val rotation = activity.display?.rotation?:0 // .windowManager.defaultDisplay.rotation
        return (getOrientation(rotation) + sensorOrientation + 270) % 360
    }

    /**
     * 0: Highest : from UHD : 3840 × 2160
     * 1: High : from full HD : 1920 x 1080
     * 2: Standard : from HD : 1280 x 720
     * 3: Low : under HD, such as SD
     */
    fun chooseOptimalSize(choices: Array<Size>, pictureQuality:Int,  ratio: Float): Size {
        val highest: MutableList<Size> = ArrayList()
        val high: MutableList<Size> = ArrayList()
        val standard: MutableList<Size> = ArrayList()
        val low: MutableList<Size> = ArrayList()

        for (option in choices) {
            if (option.width >= 3840 && option.height >= 2160 ) { // full HD and higher
                highest.add(option)
            }else if (option.width >= 1920 && option.height >= 1080 ) { // full HD and higher
                high.add(option)
            }else if (option.width >= 1280 && option.height >= 720 ) { // HD and higher
                standard.add(option)
            }else
                low.add(option)
        }
        when(pictureQuality){
            0 -> { // get highest /* prioritize order highest -> high -> standard -> low*/
                when {
                    highest.size > 0 -> return Collections.min(highest, CompareSizesByArea(ratio))
                    high.size > 0 -> return Collections.min(high, CompareSizesByArea(ratio))
                    standard.size > 0 -> return Collections.min(standard, CompareSizesByArea(ratio))
                    low.size > 0 -> return Collections.min(low, CompareSizesByArea(ratio))
                }
            }
            1 -> { // get high /* prioritize order high -> highest -> standard -> low*/
                when {
                    high.size > 0 -> return Collections.min(high, CompareSizesByArea(ratio))
                    highest.size > 0 -> return Collections.min(highest, CompareSizesByArea(ratio))
                    standard.size > 0 -> return Collections.min(standard, CompareSizesByArea(ratio))
                    low.size > 0 -> return Collections.min(low, CompareSizesByArea(ratio))
                }
            }
            2 -> { // get standard /* prioritize order standard -> high -> highest -> low*/
                when {
                    standard.size > 0 -> return Collections.min(standard, CompareSizesByArea(ratio))
                    high.size > 0 -> return Collections.min(high, CompareSizesByArea(ratio))
                    highest.size > 0 -> return Collections.min(highest, CompareSizesByArea(ratio))
                    low.size > 0 -> return Collections.min(low, CompareSizesByArea(ratio))
                }
            }
            3 -> { // get standard /* prioritize order low -> standard -> high -> highest*/
                when {
                    low.size > 0 -> return Collections.min(low, CompareSizesByArea(ratio))
                    standard.size > 0 -> return Collections.min(standard, CompareSizesByArea(ratio))
                    high.size > 0 -> return Collections.min(high, CompareSizesByArea(ratio))
                    highest.size > 0 -> return Collections.min(highest, CompareSizesByArea(ratio))
                }
            }
        }

        return choices[0]
    }

    //maxWidth: Int, maxHeight: Int,
    /**
     * using for preview size
     */
    fun chooseOptimalSize(choices: Array<Size>, textureViewWidth: Int, textureViewHeight: Int,  aspectRatio: Size): Size {
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough: MutableList<Size> = ArrayList()
        val otherSize: MutableList<Size> = ArrayList()
        for (option in choices) {
                if ((option.width <= textureViewWidth && option.height <= textureViewHeight)
                    && (option.width >= textureViewWidth/2 && option.height >= textureViewHeight/2))
                    bigEnough.add(option)
                else
                    otherSize.add(option)
        }

        return when {
            bigEnough.size > 0 -> Collections.min(bigEnough, CompareSizesByArea(aspectRatio.width.toFloat()/ aspectRatio.height.toFloat()))
            otherSize.size > 0 -> Collections.min(otherSize, CompareSizesByArea(aspectRatio.width.toFloat()/ aspectRatio.height.toFloat()))
            else -> choices[0]
        }
    }

    fun getFlashImageResource(flashState: Int): Int{
        return when(flashState){
             1 -> R.drawable.ic_camera_flash_on
             2 -> R.drawable.ic_camera_flash_off
             else -> R.drawable.ic_camera_flash_auto
         }
    }

    fun getFlashMode(flashState: Int):Int{
        return when(flashState){
            1 -> CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH
            2 -> CaptureRequest.CONTROL_AE_MODE_OFF
            else -> CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        }
    }

    fun getFlashState(flashMode: Int):Int{
        return when(flashMode){
            CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH -> 1
            CaptureRequest.CONTROL_AE_MODE_OFF -> 2
            else -> 0 //CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        }
    }


    fun getTimerImageResource(timerState: Int): Int{
        return when(timerState){
            1 -> R.drawable.ic_timer_3s
            2 -> R.drawable.ic_timer_10s
            else -> R.drawable.ic_timer
        }
    }

    fun getTimer(timerType:Int):Int{
        return when(timerType){
            1 -> 3
            2 -> 10
            else -> 0
        }
    }

    /**
     * 16:9
     * 5:3
     * 4:3
     * 3:2
     */
    fun getCameraFrameRatio(frameType:Int):Float{
        return when(frameType){
            1 -> 5f/3f
            2 -> 4f/3f
            3 -> 3f/2f
            else -> 16f/9f
        }
    }

    ///////////////////////////////////////


}