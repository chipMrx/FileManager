package com.apcc.emma.ui.dialog.common

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogInfoBinding
import com.apcc.base.dialog.BaseDialog

class InfoDialog:BaseDialog<DialogInfoBinding>() {
    override val resourceLayoutId: Int
        get() = R.layout.dialog_info

    override val TAG: String
        get() = "InfoDialog"

    companion object{
        private const val EXTRA_INFO = "extraInfo"
        fun newInstance(info:String) = InfoDialog().apply {
            val bundle = Bundle()
            bundle.putString(EXTRA_INFO, info)
            arguments = bundle
        }
    }

    val infoField = ObservableField("")

    override fun extraData() {
        arguments?.let {
            infoField.set(it.getString(EXTRA_INFO)?:"")
        }
    }

    override fun onInitView(root: View?) {
        binding.dialog = this
    }

    override fun subscribeUi() {}

    override fun allowCancel(): Boolean {
        return true
    }


    ///////////////////////////

}