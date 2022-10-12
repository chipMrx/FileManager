package com.apcc.emma.ui.dialog.common

import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogQuestionImageBinding
import com.apcc.base.dialog.BaseDialog

class QuestionImageDialog:BaseDialog<DialogQuestionImageBinding>() {
    override val resourceLayoutId: Int
        get() = R.layout.dialog_question_image

    override val TAG: String
        get() = "QuestionImageDialog"

    companion object{
        fun newInstance(callback:Callback) = QuestionImageDialog().apply {
            mCallback = callback
        }

    }

    private var mCallback:Callback?=null

    override fun extraData() {}

    override fun onInitView(root: View?) {
        binding.dialog = this
    }

    override fun subscribeUi() {}

    override fun allowCancel(): Boolean {
        return true
    }

    fun onTakePictureClick(){
        mCallback?.onRequestTakePicture()
        requestDismiss()
    }

    fun onPickPictureClick(){
        mCallback?.onRequestPickPicture()
        requestDismiss()
    }

    private fun requestDismiss(){
        try {
            dismissAllowingStateLoss()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    ///////////////////////////
    interface Callback{
        fun onRequestTakePicture()
        fun onRequestPickPicture()

    }
}