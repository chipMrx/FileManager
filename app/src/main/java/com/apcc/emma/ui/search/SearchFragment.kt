package com.apcc.emma.ui.search

import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.SearchFragmentBinding
import com.apcc.base.fragment.BaseFragment

class SearchFragment : BaseFragment<SearchFragmentBinding, SearchViewModel>() {

    override val resourceLayoutId: Int
        get() = R.layout.search_fragment

    override fun onInitView(root: View?) {
    }

    override fun subscribeUi() {
        binding.edtSearch.requestFocus()
    }

}
