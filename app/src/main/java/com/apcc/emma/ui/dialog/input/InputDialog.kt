package com.apcc.emma.ui.dialog.input

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.ObservableField
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogInputBinding
import com.apcc.base.dialog.BaseDialog
import java.lang.Exception

class InputDialog:BaseDialog<DialogInputBinding>() {
    override val resourceLayoutId: Int
        get() = R.layout.dialog_input

    override val TAG: String
        get() = "InputDialog"

    companion object{
        private const val EXTRA_TITLE = "extraTitle"
        private const val EXTRA_VAL = "extraVal"
        private const val EXTRA_MAX_LENGTH = "extraMaxLength"

        fun newInstance(title:String? = null, strVal:String?=null , maxLength:Int = -1 , callback:Callback?=null): InputDialog{
            val dialog = InputDialog()
            val bundle = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putString(EXTRA_VAL, strVal)
                putInt(EXTRA_MAX_LENGTH, maxLength)
            }
            dialog.arguments = bundle
            dialog.mCallback = callback
            return dialog
        }
    }

    var mTitle:ObservableField<String> = ObservableField("")
    var mStrVal:ObservableField<String> = ObservableField("")
    var mMaxLength = 0
    private var mCallback:Callback?=null

    override fun extraData() {
        arguments?.let {
            mTitle.set(it.getString(EXTRA_TITLE, ""))
            mStrVal.set(it.getString(EXTRA_VAL, ""))
            mMaxLength = it.getInt(EXTRA_MAX_LENGTH, 0)// default is un set max length
        }
    }

    override fun onInitView(root: View?) {
    }

    override fun subscribeUi() {
        binding.dialog = this
        binding.edtInput.setMaxLength(mMaxLength)
    }

    override fun configDialogAttributes(attributes: WindowManager.LayoutParams){
        attributes.gravity = Gravity.CENTER
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
    }


    fun doneInput(){
        try {
            mCallback?.onInputDone(binding.edtInput.text.toString())
            dismissAllowingStateLoss()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////
    interface Callback{
        fun onInputDone(strVal:String)
    }


}