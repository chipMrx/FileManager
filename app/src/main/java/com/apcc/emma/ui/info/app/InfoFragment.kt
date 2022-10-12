package com.apcc.emma.ui.info.app

import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.InfoFragmentBinding
import com.apcc.base.fragment.BaseFragment

class InfoFragment : BaseFragment<InfoFragmentBinding, InfoViewModel>() {

    override val resourceLayoutId: Int
        get() = R.layout.info_fragment


    override fun onInitView(root: View?) {
        screenName = getString(R.string.title_appInfo)
        binding.viewModel = viewModel
    }

    override fun subscribeUi() {
        showProgress(false)
    }
}
