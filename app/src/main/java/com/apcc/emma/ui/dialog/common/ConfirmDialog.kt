package com.apcc.emma.ui.dialog.common

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import com.apcc.base.dialog.BaseDialog
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogConfirmBinding

class ConfirmDialog:BaseDialog<DialogConfirmBinding>() {
    override val resourceLayoutId: Int
        get() = R.layout.dialog_confirm

    override val TAG: String
        get() = "ConfirmDialog"

    companion object{
        private const val EXTRA_TITLE = "extraTitle"
        private const val EXTRA_CONTENT = "extraContent"
        private const val EXTRA_BTN_DENY = "extraBtnDeny"
        private const val EXTRA_BTN_OK = "extraBtnOk"

        /**
         * @btnDeny: default send null~ "Cancel" text
         * @btnOk: default send null~ "Ok" text
         */
        fun newInstance(title:String = "", content:String = "",
                        btnDeny:String?=null, btnOk:String?=null,
                        callback:Callback) = ConfirmDialog().apply {
            val bundle = Bundle()
            bundle.putString(EXTRA_TITLE, title)
            bundle.putString(EXTRA_CONTENT, content)
            btnDeny?.let { bundle.putString(EXTRA_BTN_DENY, btnDeny) }
            btnOk?.let { bundle.putString(EXTRA_BTN_OK, btnOk) }
            arguments = bundle
            mCallback = callback
        }
    }

    val titleField = ObservableField("")
    val contentField = ObservableField("")
    val btnDenyField = ObservableField("")
    val btnOkField = ObservableField("")

    private var mCallback:Callback?=null

    override fun extraData() {
        arguments?.let { bundle->
            titleField.set(bundle.getString(EXTRA_TITLE)?:"")
            contentField.set(bundle.getString(EXTRA_CONTENT)?:"")
            val deny = bundle.getString(EXTRA_BTN_DENY)
            if (deny == null){
                btnDenyField.set(getString(R.string.lbl_cancel))
            }else{
                btnDenyField.set(deny)
            }
            val ok = bundle.getString(EXTRA_BTN_OK)
            if (ok == null){
                btnOkField.set(getString(R.string.lbl_ok))
            }else{
                btnOkField.set(ok)
            }
        }
    }

    override fun onInitView(root: View?) {
        binding.dialog = this
    }

    override fun subscribeUi() {}

    fun confirmDeny(){
        try {
            mCallback?.onConfirmCancel()
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun confirmOk(){
        try {
            mCallback?.onConfirmOk()
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ///////////////////////////
    ///////////////////////////
    interface Callback{
        fun onConfirmCancel()
        fun onConfirmOk()
    }
}