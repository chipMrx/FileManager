package com.apcc.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings.Secure.ANDROID_ID
import android.provider.Settings.Secure.getString
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.apcc.emma.R
import com.apcc.framework.AppManager
import com.apcc.view.XToast


object Util {

    //////////////////////////////////////
    // system
    ////////////////////////////////////////

    fun cleanAppTask(context: Context){
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        if (activityManager != null) {
            val tasks = activityManager.appTasks
            if (tasks != null && tasks.size > 0) {
                for (i in tasks.indices) {
                    tasks[i].finishAndRemoveTask()
                }
            }
        }
    }

    /**
     * return true if copied!
     */
    fun copyToClipBoard(context: Context?, view: View?, data:Double?):Boolean{
        return copyToClipBoard(context, view, (data?:0.0).toString())
    }
    fun copyToClipBoard(context: Context?, view: View?, data:String?):Boolean{
        context?.let {ctx->
            if (!TextUtils.isEmpty(data)){
                val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Copied!", data)
                clipboard.setPrimaryClip(clip)
                XToast.showInfo(view,  R.string.lbl_copied )
                return true
            }
        }
        XToast.showInfo(view,  R.string.lbl_nothingCopied )
        return false
    }

    //////////////////////////////////////
    // device
    ////////////////////////////////////////

    @SuppressLint("HardwareIds")
    fun getDeviceID():String{
        return getDeviceID(AppManager.instance)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceID(context: Context):String{
        return getString(context.contentResolver, ANDROID_ID)
    }

    /**
     * return width of screen as pixels
     */
    fun getDeviceWidth(activity: Activity):Int{
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels
//        val width = displayMetrics.widthPixels
        return displayMetrics.heightPixels
    }

    fun isConnected(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    //////////////////////////////////////
    // keyboard
    ////////////////////////////////////////

    fun hideSoftKeyboard(view: View?) {
        view?.let {v->
            val inputMethodManager = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * Set up touch listener for non-text box views to hide keyboard.
     */
    fun hideKeyboardOutside(viewParent: View?) {
        viewParent?.let {
            if (viewParent !is EditText) {
                viewParent.setOnTouchListener { v: View, _: MotionEvent? ->
                    hideSoftKeyboard(viewParent)
                    v.clearFocus()
                    false
                }
            }
            if (viewParent is ViewGroup) {
                for (i in 0 until viewParent.childCount) {
                    val innerView = viewParent.getChildAt(i)
                    hideKeyboardOutside(innerView)
                }
            }
        }
    }
    ////////////////////////////////////////
    //
    ///////////////////////////////////////////
    /**
     * !Warning:
     * @deprecated As of {@link android.os.Build.VERSION_CODES#O}, this method
     * is no longer available to third party applications.  For backwards compatibility,
     * it will still return the caller's own services.
     * ********
     * So it still working good
     */
    fun checkServiceRunning(context: Context, serviceClassName:String):Boolean{
        val manager = ContextCompat.getSystemService(context, ActivityManager::class.java)
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClassName.equals(service.service.className, false)) {
                return true
            }
        }
        return false
    }

    ////////////////////////////////////////
    // resource
    ///////////////////////////////////////////

    fun getInt(context: Context, resource:Int):Int{
        return context.resources.getInteger(resource)
    }

    fun getDimension(context: Context, resource:Int) = context.resources.getDimension(resource)

    //fun getColor(context: Context, resource:Int) = ContextCompat.getColor(context, resource)
    fun getColor(context: Context, resource:Int) = AppCompatResources.getColorStateList(context, resource)
    fun getDrawable(context: Context, resource:Int) = AppCompatResources.getDrawable(context, resource)

    fun <T : Any>lookingForGirlFriend(clz: Class<T>, girlFriend:String):Any?{
        return clz.getDeclaredMethod(girlFriend)
    }


    fun  <T : Any>makeLoveWithGirlFriend(clz: Class<T>, any:Any, girlFriend:String, ttls:Boolean = true, vararg anyCondoms:Any):Boolean{
        try {
            if (AppManager.aczzu(clz.name, ttls)){
                if (anyCondoms.isNotEmpty()){
                    clz.getDeclaredMethod(girlFriend).invoke(any, anyCondoms)
                }else{
                    clz.getDeclaredMethod(girlFriend).invoke(any)
                }
                return true
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false
    }

    ////////////////////////////////////////
    // external
    ///////////////////////////////////////////


    external fun cappuccino()
}