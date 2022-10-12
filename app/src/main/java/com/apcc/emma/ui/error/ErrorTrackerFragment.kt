package com.apcc.emma.ui.error

import android.content.Intent
import android.view.View
import com.apcc.emma.R
import com.apcc.data.ErrorTracking
import com.apcc.emma.databinding.ErrorTrackerFragmentBinding
import com.apcc.base.fragment.BaseFragment
import com.apcc.emma.ui.file.FileActivity

class ErrorTrackerFragment : BaseFragment<ErrorTrackerFragmentBinding, ErrorTrackerViewModel>(), Action {
    override val resourceLayoutId: Int
        get() = R.layout.error_tracker_fragment

    override fun onInitView(root: View?) {
        screenName = getString(R.string.title_appCrash)
    }

    override fun subscribeUi() {
        binding.action = this
        binding.viewModel = viewModel

        val error:ErrorTracking? = activity?.intent?.getParcelableExtra(ErrorTrackerActivity.EXTRA_ERROR_TRACKING)
        viewModel.buildString(error)

        viewModel.resultsSendError.observe(viewLifecycleOwner) { response ->
            if (viewModel.handlerSendError(response))
                backToApp()
        }

        viewModel.isWaiting.observe(viewLifecycleOwner) { isShow ->
            showProgress(isShow)
        }
    }

    override fun enableHomeAsBackButton(): Boolean {
        return false
    }

    override fun sendReport() {
        /*reopen app*/
        viewModel.sendError(binding.edtComment.text.toString())
    }

    override fun printDebugLog() {
        viewModel.printLog()
    }

    override fun backToApp() {
        val intent = Intent(activity, FileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        activity?.finish()
    }


}
