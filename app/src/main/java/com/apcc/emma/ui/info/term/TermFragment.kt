package com.apcc.emma.ui.info.term

import android.content.Context
import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.TermFragmentBinding
import com.apcc.emma.ui.info.InfoActivityCallback
import com.apcc.base.fragment.BaseFragment

class TermFragment : BaseFragment<TermFragmentBinding, TermViewModel>(), Action {

    override val resourceLayoutId: Int
        get() = R.layout.term_fragment

    private var mCallback: InfoActivityCallback?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InfoActivityCallback)
            mCallback = context
    }

    override fun onInitView(root: View?) {
        screenName = getString(R.string.title_term)
    }

    override fun subscribeUi() {
        binding.action = this
        showProgress(false)
    }

    override fun onAcceptTerm() {
        mCallback?.popBack(TermFragment::class.java.name)
    }

}
