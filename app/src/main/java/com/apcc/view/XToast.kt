package com.apcc.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.StringDef
import com.apcc.emma.R
import com.apcc.utils.TextType
import com.google.android.material.snackbar.Snackbar


class XToast(context: Context) : Toast(context) {

    companion object{

        private var mSnackbar : Snackbar? = null

        fun cancelInfo(){
            mSnackbar?.dismiss()// .cancel()
        }
        /////////////////////////////////
        /// info type
        /////////////////////////////////////
        fun showInfo(view: View?, messageSource: Int){
            view?.let { v->
                show(view, v.context.getString(messageSource), InfoType.INFO)
            }
        }
        fun showInfo(view: View?, message: String?){
            show(view, message, InfoType.INFO)
        }
        /////////////////////////////////
        /// susscess type
        /////////////////////////////////////
        fun showSuccessInfo(view: View?, messageSource: Int){
            view?.let { v->
                show(view, v.context.getString(messageSource), InfoType.SUCCESS)
            }
        }
        fun showSuccessInfo(view: View?, message: String){
            show(view, message, InfoType.SUCCESS)
        }
        /////////////////////////////////
        /// warning type
        /////////////////////////////////////
        fun showWarningInfo(view: View?, messageSource: Int){
            view?.let { v->
                show(view, v.context.getString(messageSource), InfoType.WARNING)
            }
        }
        fun showWarningInfo(view: View?, message: String){
//            show(context, message, InfoType.WARN/ING)
            show(view, message, InfoType.WARNING)
        }
        /////////////////////////////////
        /// error type
        /////////////////////////////////////
        fun showErrorInfo(view: View?, messageSource: Int){
            view?.let { v->
                show(view, v.context.getString(messageSource), InfoType.ERROR)
            }
        }
        fun showErrorInfo(view: View?, message: String){
            //show(context, message, InfoType.ERROR)
            show(view, message, InfoType.ERROR)
        }
        /////////////////////////////////
        /// for showing
        /////////////////////////////////////
        private fun show(view: View?, message: String?, type: Int) {
            if (view == null || message == null || message.isEmpty())
                return
            cancelInfo()
            mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

            mSnackbar?.let { snb->
                val snackBarLayout:Snackbar.SnackbarLayout = snb.view as Snackbar.SnackbarLayout
                val params: FrameLayout.LayoutParams = snackBarLayout.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.CENTER
                params.width = FrameLayout.LayoutParams.WRAP_CONTENT
                params.height = FrameLayout.LayoutParams.WRAP_CONTENT
                snackBarLayout.layoutParams = params

                snackBarLayout.removeAllViews()
                snackBarLayout.setBackgroundColor(Color.TRANSPARENT)

                val snackBarCustomV: View = LayoutInflater.from(view.context).inflate(R.layout.view_toast, null, false)
                val icon:XIcon = snackBarCustomV.findViewById(R.id.icState)
                val txtMessage:XTextView = snackBarCustomV.findViewById(R.id.txtMessage)

                txtMessage.text = message
                when(type){
                    InfoType.SUCCESS -> {
                        icon.setImageResource(R.drawable.ic_check)
                        icon.setTintColor(R.color.colorSuccess, true)
                        snackBarCustomV.setBackgroundResource(R.drawable.bg_toast_success)
                        txtMessage.setTextType(TextType.TEXT_NORMAL_SUCCESS, 0)
                    }
                    InfoType.WARNING -> {
                        icon.setImageResource(R.drawable.ic_warning)
                        icon.setTintColor(R.color.colorPrimary, true)
                        snackBarCustomV.setBackgroundResource(R.drawable.bg_toast_warning)
                        txtMessage.setTextType(TextType.TEXT_NORMAL_IMPORTANT, 0)
                    }
                    InfoType.ERROR -> {
                        icon.setImageResource(R.drawable.ic_error)
                        icon.setTintColor(R.color.colorAccent, true)
                        snackBarCustomV.setBackgroundResource(R.drawable.bg_toast_error)
                        txtMessage.setTextType(TextType.TEXT_NORMAL_ACCENT, 0)
                    }
                    else->{
                        icon.setImageResource(R.drawable.ic_information)
                        icon.setTintColor(R.color.colorImportant, true)
                        snackBarCustomV.setBackgroundResource(R.drawable.bg_toast_info)
                        txtMessage.setTextType(TextType.TEXT_NORMAL_IMPORTANT, 0)
                    }
                }

                snackBarLayout.setPadding(0, 0, 0, 0)
                snackBarLayout.addView(snackBarCustomV, 0)
                snb.show()
            }
        }
    }


    //////////////////////////
    @StringDef()
    @Retention(AnnotationRetention.SOURCE)
    annotation class InfoType{
        companion object{
            const val SUCCESS = 1
            const val WARNING = 2
            const val ERROR = 3
            const val INFO = 0 // default
        }
    }
}