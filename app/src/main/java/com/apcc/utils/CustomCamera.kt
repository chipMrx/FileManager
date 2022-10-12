package com.apcc.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.RggbChannelVector
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.apcc.framework.CacheManager
import com.apcc.view.XTextureView
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow


class CustomCamera(
    val activity: Activity,
    private val textureView: XTextureView,
    val cameraListener: CameraListener,
    lensFacing: Int = CameraCharacteristics.LENS_FACING_BACK
) {

    companion object{
        const val TAG = "LogCoCamera"

        private const val STATE_PREVIEW = 0
        private const val STATE_WAITING_LOCK = 1
        private const val STATE_CAPTURED = 2
    }


    ////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////

    private var mCameraId: String? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mCameraDevice: CameraDevice? = null
    private var mPreviewSize: Size? = null
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mPreviewRequest: CaptureRequest? = null
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private val mCameraOpenCloseLock = Semaphore(1)
    private var mFlashSupported = false
    private var mFlashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
    private var mCameraFrame = 16f/9f
    private var mPictureQuality = 0 // highest
    private var mSensorOrientation = 0
    private var mState = STATE_PREVIEW
    private var mLensFacing = CameraCharacteristics.LENS_FACING_BACK

    init {
        mLensFacing = lensFacing
    }

    ////////////////////////////////////////////////////////////////////
    //// listener
    /////////////////////////////////////////////////////////////////////

    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            initCamera()
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            cameraListener.cameraError()
        }
    }

    private val mCaptureCallback: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        private fun process(result: CaptureResult) {
            when (mState) {
                STATE_CAPTURED -> {
                }
                STATE_PREVIEW -> {
                }
                STATE_WAITING_LOCK -> {
                    capturePicture()
                }
            }
        }
        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            process(partialResult)
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            process(result)
        }
    }

    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        mBackgroundHandler?.post {
            cameraListener.onImageAvailable(BitmapUtils.decodeBitmap(reader.acquireNextImage()))
        }
    }

    ////////////////////////////////////////////////////////////////////
    // for camera
    // init -> open -> preview -> capture -> close
    /////////////////////////////////////////////////////////////////////


    init {
        textureView.surfaceTextureListener = mSurfaceTextureListener
    }


    fun initCamera(){
        if (mCameraDevice != null)// // has init
            return
        if (textureView.isAvailable) {
            initDataSetting()
            startBackgroundThread()
            openCamera(textureView.width, textureView.height)
        }else{
            textureView.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    fun initDataSetting(){
        mFlashMode = CameraUtil.getFlashMode(CacheManager.getCameraFlash(activity))
        mCameraFrame = CameraUtil.getCameraFrameRatio(CacheManager.getCameraFrame(activity))
        mPictureQuality = CacheManager.getPictureQuality(activity)
    }

    fun updateSetting(){
        initDataSetting()
        // do some thing else to update
        closeCamera()
        initCamera()
//        if (mPreviewRequestBuilder != null)
//            setupFlash(mPreviewRequestBuilder)
    }


    /**
     * Open the camera specified by [mCameraId].
     */
    @SuppressLint("MissingPermission")
    fun openCamera(width: Int, height: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraListener.permissionFail()
            return
        }
        if (setUpCameraOutputs(width, height)){
            configureTransform(width, height)
            val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    //throw RuntimeException("Time out waiting to lock camera opening.")
                    cameraListener.setupFail()
                    return
                }
                manager.openCamera(mCameraId!!, mStateCallback, mBackgroundHandler)
                cameraListener.cameraOpened()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
                Logger.e("openCamera => CameraAccessException")
                cameraListener.setupFail()
            } catch (e: InterruptedException) {
                Logger.e("openCamera => InterruptedException;  Interrupted while trying to lock camera opening.")
                cameraListener.setupFail()
            }
        }
    }

    fun requestTakePicture() {
        try {
            if (mState != STATE_WAITING_LOCK){
                // This is how to tell the camera to lock focus.
                mPreviewRequestBuilder?.set(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START
                )
                // Tell #mCaptureCallback to wait for the lock.
                mCaptureSession?.capture(
                    mPreviewRequestBuilder!!.build(),
                    mCaptureCallback,
                    mBackgroundHandler
                )
                mState = STATE_WAITING_LOCK
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * [.mCaptureCallback] from both [.lockFocus].
     */
    private fun capturePicture() {
        try {
            if (mCameraDevice == null) return
            // This is the CaptureRequest.Builder that we use to take a picture.
            val captureBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(mImageReader!!.surface)

            // Use the same AE and AF modes as the preview.
            //captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            captureBuilder.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_CANCEL
            )
            setupFlash(captureBuilder)

            // Orientation
            captureBuilder.set(
                CaptureRequest.JPEG_ORIENTATION, CameraUtil.getOrientation(
                    activity,
                    mSensorOrientation
                )
            )
            val captureCallback: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    ession: CameraCaptureSession,
                    equest: CaptureRequest,
                    esult: TotalCaptureResult
                ) {
                    //unlockFocus()
                    stopPreview()
                }
            }
            mCaptureSession!!.capture(captureBuilder.build(), captureCallback, null)
            mState = STATE_CAPTURED
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            cameraListener.cameraError()
        }catch (e: Exception){
            e.printStackTrace()
            cameraListener.cameraError()
        }
    }

    /**
     * using it after take picture
     */
    fun requestContinuousCamera() {
        try {
            if (mPreviewRequest == null || mCaptureSession == null){
                initCamera()
                return
            }
            // Reset the auto-focus trigger
            mPreviewRequestBuilder!!.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
            )
            setupFlash(mPreviewRequestBuilder)
            mCaptureSession!!.capture(
                mPreviewRequestBuilder!!.build(),
                mCaptureCallback,
                mBackgroundHandler
            )
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW
            mCaptureSession!!.setRepeatingRequest(
                mPreviewRequest!!,
                mCaptureCallback,
                mBackgroundHandler
            )
            cameraListener.cameraUnlocked()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            cameraListener.cameraError()
        }catch (e: Exception){
            e.printStackTrace()
            cameraListener.cameraError()
        }
    }



    /**
     * Closes the current [CameraDevice].
     */
    fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
        }catch (e: Exception){
            e.printStackTrace()
        } finally {
            mCameraOpenCloseLock.release()
        }

        try {
            mCaptureSession?.close()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            mCaptureSession = null
        }

        try {
            mCameraDevice?.close()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            mCameraDevice = null
        }

        try {
            mImageReader?.close()
        }catch (e: Exception){
            e.printStackTrace()
        } finally {
            mImageReader = null
        }
        stopBackgroundThread()
    }


    ////////////////////////////////////////////////////////////////////
    //// support function
    /////////////////////////////////////////////////////////////////////

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    /**
     * Stops the background thread [Handler].
     */
    private fun stopBackgroundThread() {
        try {
            mBackgroundThread?.quitSafely()
            mBackgroundThread?.join()// waiting for thread done
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }finally {
            mBackgroundThread = null
            mBackgroundHandler = null
        }
    }

    fun stopPreview(){
        mCaptureSession?.apply {
            stopRepeating()
            //abortCaptures()// don't do it// CameraCaptureSession abortCapture very bad performance
            // You don't need to call abortCaptures to change camera capture parameters
            // you generally only need it if you want to change to a new capture session quickly,
            // and that's only when you have new target Surfaces. Such as switching from photo mode to video mode.
        }
    }

    private fun setUpCameraOutputs(width: Int, height: Int):Boolean {
        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this app.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == mLensFacing) {
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                    // For still image captures, we use the largest available size.
                    val pictureOutPutSize = CameraUtil.chooseOptimalSize(
                        map.getOutputSizes(ImageFormat.JPEG),
                        mPictureQuality, mCameraFrame
                    )

                        /*Collections.max(
                        listOf(*map.getOutputSizes(ImageFormat.JPEG)),
                        CompareSizesByArea()
                    )*/
                    mImageReader = ImageReader.newInstance(
                        pictureOutPutSize.width,
                        pictureOutPutSize.height,
                        ImageFormat.JPEG,
                        2
                    )
                    mImageReader!!.setOnImageAvailableListener(
                        mOnImageAvailableListener,
                        mBackgroundHandler
                    )

                    // Find out if we need to swap dimension to get the preview size relative to sensor
                    // coordinate.

                    // Find out if we need to swap dimension to get the preview size relative to sensor
                    // coordinate.
                    val displayRotation = activity.display?.rotation?:0
                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                    var swappedDimensions = false
                    when (displayRotation) {
                        Surface.ROTATION_0, Surface.ROTATION_180 -> if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true
                        }
                        Surface.ROTATION_90, Surface.ROTATION_270 -> if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true
                        }
                        else -> Logger.e("Display rotation is invalid: $displayRotation")
                    }
                    //val displaySize = activity.windowManager.currentWindowMetrics.bounds
                    var rotatedPreviewWidth = width
                    var rotatedPreviewHeight = height
                    if (swappedDimensions) {
                        rotatedPreviewWidth = height
                        rotatedPreviewHeight = width
                    }
                    // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                    // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                    // garbage capture data.
                    mPreviewSize = CameraUtil.chooseOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        rotatedPreviewWidth,
                        rotatedPreviewHeight,
                        pictureOutPutSize
                    )

                    // We fit the aspect ratio of TextureView to the size of preview we picked.
                    if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        textureView.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
                    } else {
                        textureView.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
                    }

                    // Check if the flash is supported.
                    val available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    mFlashSupported = available ?: false
                    mCameraId = cameraId
                    return true
                }
            }
            cameraListener.lensFacingError(mLensFacing)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            cameraListener.setupFail()
        } catch (e: NullPointerException) {
            cameraListener.errorNotSupported()
        }
        return false
    }

    private fun colorTemperature(whiteBalance: Int): RggbChannelVector {
        val temperature = (whiteBalance / 100).toFloat()
        var red: Float
        var green: Float
        var blue: Float

        //Calculate red
        if (temperature <= 66) red = 255f else {
            red = temperature - 60
            red = (329.698727446 * red.toDouble().pow(-0.1332047592)).toFloat()
            if (red < 0) red = 0f
            if (red > 255) red = 255f
        }


        //Calculate green
        if (temperature <= 66) {
            green = temperature
            green = (99.4708025861 * ln(green.toDouble()) - 161.1195681661).toFloat()
            if (green < 0) green = 0f
            if (green > 255) green = 255f
        } else {
            green = temperature - 60
            green = (288.1221695283 * green.toDouble().pow(-0.0755148492)).toFloat()
            if (green < 0) green = 0f
            if (green > 255) green = 255f
        }

        //calculate blue
        if (temperature >= 66) blue = 255f else if (temperature <= 19) blue = 0f else {
            blue = temperature - 10
            blue = (138.5177312231 * ln(blue.toDouble()) - 305.0447927307).toFloat()
            if (blue < 0) blue = 0f
            if (blue > 255) blue = 255f
        }
        Logger.d("red=$red, green=$green, blue=$blue")
        return RggbChannelVector(red / 255 * 2, green / 255, green / 255, blue / 255 * 2)
    }


    private fun setupFlash(requestBuilder: CaptureRequest.Builder?) {
//        val rggb: RggbChannelVector = colorTemperature(698)
//
//        requestBuilder?.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggb)


        if (mFlashSupported) {
            requestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, mFlashMode)
            updateCameraState(1, CameraUtil.getFlashState(mFlashMode))
        }else{
            updateCameraState(1, 2, false) // off
        }
    }


    /**
     * Creates a new [CameraCaptureSession] for camera preview.
     */
    private fun createCameraPreviewSession() {
        try {
            val texture: SurfaceTexture = textureView.surfaceTexture!!
            texture.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
            val surface = Surface(texture)
            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)

            val stateCallBack = object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    // The camera is already closed
                    if (mCameraDevice == null) {
                        return
                    }
                    // When the session is ready, we start displaying the preview.
                    mCaptureSession = cameraCaptureSession
                    try {
                        // Auto focus should be continuous for camera preview.
                        mPreviewRequestBuilder!!.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        mPreviewRequestBuilder!!.set(
                            CaptureRequest.CONTROL_AF_TRIGGER,
                            CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
                        )
                        // Flash is automatically enabled when necessary.
                        setupFlash(mPreviewRequestBuilder)
                        // Finally, we start displaying the camera preview.
                        mPreviewRequest = mPreviewRequestBuilder!!.build()
                        mCaptureSession!!.setRepeatingRequest(
                            mPreviewRequest!!,
                            mCaptureCallback,
                            mBackgroundHandler
                        )
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                        cameraListener.cameraError()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        cameraListener.cameraError()
                    }
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    //
                    Logger.e("createCameraPreviewSession -> onConfigureFailed")
                }
            }

            // Here, we create a CameraCaptureSession for camera preview.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) { // added in api level 28
                mCameraDevice?.createCaptureSession(
                    SessionConfiguration(
                        SessionConfiguration.SESSION_REGULAR,
                        listOf(
                            OutputConfiguration(surface),
                            OutputConfiguration(mImageReader!!.surface)
                        ),
                        { command ->
                            // The executor which should be used to invoke the callback. In general it is,
                            // recommended that camera operations are not done on the main (UI) thread.
                            command.run()
                        },
                        stateCallBack
                    )
                )
            }else{
                mCameraDevice!!.createCaptureSession(
                    listOf(surface, mImageReader!!.surface),
                    stateCallBack,
                    null
                )
            }

//            mCameraDevice!!.createCaptureSession(listOf(surface, mImageReader!!.surface), stateCallBack, null)//,
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Logger.e("createCameraPreviewSession => CameraAccessException")
            cameraListener.setupFail()
        } catch (e: Exception){
            e.printStackTrace()
            Logger.e("createCameraPreviewSession => Exception")
            cameraListener.setupFail()
        }
    }

    fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (null == mPreviewSize) {
            return
        }
        val rotation = activity.display?.rotation?:0
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(
            0f,
            0f,
            mPreviewSize!!.height.toFloat(),
            mPreviewSize!!.width.toFloat()
        )
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = max(
                viewHeight.toFloat() / mPreviewSize!!.height,
                viewWidth.toFloat() / mPreviewSize!!.width
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90 * (rotation - 2).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    fun getFlashState() = CameraUtil.getFlashState(mFlashMode)

    fun requestChangeCamera(){
        if (mLensFacing == CameraCharacteristics.LENS_FACING_BACK)
            mLensFacing = CameraCharacteristics.LENS_FACING_FRONT
        else
            mLensFacing = CameraCharacteristics.LENS_FACING_BACK

        closeCamera()
        initCamera()
    }



    private fun updateCameraState(type: Int, state: Int, supportAble: Boolean = true){
        activity.runOnUiThread {
            cameraListener.updateCameraState(type, state, supportAble)
        }
    }

    ////////////////////////////////////////////////////////////////////
    //// listener
    /////////////////////////////////////////////////////////////////////
    interface CameraListener{
        //// error
        fun errorNotSupported()

        /**
         * request to re-init camera
         */
        fun setupFail()
        /**
         * request to re-init camera
         */
        fun lensFacingError(lensFacing: Int)

        /**
         * permission not granted
         */
        fun permissionFail()

        /**
         * while using we get error from camera
         */
        fun cameraError()

        /// state
        fun cameraOpened()
        fun cameraUnlocked()
        fun onImageAvailable(bitmap: Bitmap?)

        /**
         * 1: flash; 0: auto , 1:on, 2: off
         * 2: camera front / hide
         */
        fun updateCameraState(type: Int, state: Int, supportAble: Boolean = true){}
    }
}