package com.apcc.emma.ui.dialog.setting

import android.view.View
import android.view.WindowManager
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogSettingCameraBinding
import com.apcc.utils.Util
import com.apcc.base.dialog.BaseDialogVM

class CameraSettingDialog : BaseDialogVM<DialogSettingCameraBinding, SettingViewModel>(), Action{
    override val resourceLayoutId: Int
        get() = R.layout.dialog_setting_camera
    override val TAG: String
        get() = "CameraSettingDialog"

    companion object {
        fun newInstance(callback: Callback? = null) = CameraSettingDialog().apply {
            mCallback = callback
        }
    }

    private var mCallback: Callback? = null

    override fun extraData() {
        arguments?.let {}
    }

    override fun onInitView(root: View?) {
        binding.viewModel = viewModel
        binding.action = this
    }

    override fun subscribeUi() {
        viewModel.initCameraSettingData()
    }

    override fun configDialogAttributes(attributes: WindowManager.LayoutParams) {
        super.configDialogAttributes(attributes)
        attributes.width = resources.displayMetrics.widthPixels * Util.getInt(requireContext(), R.integer.dialogWidthPercentBig)/100
    }

    override fun onDoneClick() {
        viewModel.saveCameraSetting()
        doFinish()
    }


    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////

    private fun doFinish() {
        try {
            mCallback?.onSettingComplete()
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////

    interface Callback {
        fun onSettingComplete()
    }


}