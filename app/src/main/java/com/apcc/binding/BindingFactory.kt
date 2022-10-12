package com.apcc.binding

import androidx.databinding.BindingAdapter
import com.apcc.utils.DataHelper
import com.apcc.view.*

/**
 * Data Binding adapters specific to the app.
 */
object BindingFactory {

    /////////////////////////////////////////////////////////////
    // XIcon             ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JvmStatic
    @BindingAdapter("imagePath")
    fun setImageUrl(view: XIcon, path: String?) {
        view.loadByPath(path)
    }

    @JvmStatic
    @BindingAdapter("imageDefault")
    fun setImageDefault(view: XIcon, resource:Int) {
        view.setImageDefault(resource)
    }

    /////////////////////////////////////////////////////////////
    // XImage             ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JvmStatic
    @BindingAdapter("imagePath")
    fun setImageUrl(view: XImage, path: String?) {
        view.loadByPath(path)
    }

    @JvmStatic
    @BindingAdapter("imageDefault")
    fun setImageDefault(view: XImage, resource:Int) {
        view.setImageDefault(resource)
    }
    /////////////////////////////////////////////////////////////
    // XInput             ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JvmStatic
    @BindingAdapter("android:enabled")
    fun setEnable(view: XInput, enabled:Boolean) {
        view.setEnable(enabled)
    }

    @JvmStatic
    @BindingAdapter("error")
    fun setError(view: XInput, errorMessage:String ) {
        view.setError(errorMessage)
    }

    @JvmStatic
    @BindingAdapter("setTimeString")
    fun setTimeString(view: XInput, time:String ) {
        view.setTimeString(time)
    }

    @JvmStatic
    @BindingAdapter("timeMillis")
    fun setTime(view: XInput, time:Long ) {
        view.setTimeMillis(time)
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(view: XInput, textVal:String? ) {
        view.setText(textVal)
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(view: XInput, numberVal:Double? ) {
        view.setText(DataHelper.numberAsText(numberVal))
    }
    @JvmStatic
    @BindingAdapter("parentAble")
    fun setText(view: XInput, able:Boolean? ) {
        view.setParentAble(able == true)
    }



    /////////////////////////////////////////////////////////////
    // XProgress             ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(view: XProgress, textVal:String? ) {
        view.setText(textVal)
    }

    /////////////////////////////////////////////////////////////
    // XGender             ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JvmStatic
    @BindingAdapter("android:enabled")
    fun setEnable(view: XGender, enabled:Boolean) {
        view.setEnable(enabled)
    }


    @JvmStatic
    @BindingAdapter("gender")
    fun setGender(view: XGender, gender:Int ) {
        view.setGender(gender)
    }


    /////////////////////////////////////////////////////////////
    // XRepeat             ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    @JvmStatic
    @BindingAdapter("repeatType")
    fun setRepeatType(view: XRepeat, type:Int? ) {
        view.setRepeatType(type)
    }


}

