package com.apcc.emma.ui.info.license

import android.content.Context
import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.LicenseFragmentBinding
import com.apcc.emma.ui.info.InfoActivityCallback
import com.apcc.base.fragment.BaseFragment

class LicenseFragment : BaseFragment<LicenseFragmentBinding, LicenseViewModel>(), Action {

    override val resourceLayoutId: Int
        get() = R.layout.license_fragment

    private var mCallback: InfoActivityCallback?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InfoActivityCallback)
            mCallback = context
    }
    override fun onInitView(root: View?) {
        screenName = getString(R.string.title_license)
    }

    override fun subscribeUi() {
        binding.action = this
        showProgress(false)
    }

    override fun onAcceptLicense() {
        mCallback?.popBack(LicenseFragment::class.java.name)
    }

}
