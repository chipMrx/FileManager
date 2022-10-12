package com.apcc.base.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.MotionEvent

class AbsDialog : Dialog {

    var mDialogEventListener: DialogEventListener? = null

    constructor(context: Context):super(context)
    constructor(context: Context, themeResId: Int):super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ):super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mDialogEventListener != null
            && ev.action == MotionEvent.ACTION_OUTSIDE
        ) {
            mDialogEventListener!!.onTouchOutside()
        }
        return super.dispatchTouchEvent(ev)
    }

    //////////////////////////////////////
    ////// inner class
    //////////////////////////////////////
    interface DialogEventListener {
        fun onTouchOutside()
    }
}