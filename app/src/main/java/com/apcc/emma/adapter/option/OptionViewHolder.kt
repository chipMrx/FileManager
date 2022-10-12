package com.apcc.emma.adapter.option

import android.view.View
import com.apcc.data.Option
import com.apcc.emma.databinding.ItemOptionBinding
import com.apcc.base.adapter.BaseAdapter
import com.apcc.base.adapter.BaseViewHolder


class OptionViewHolder(view: View)
    : BaseViewHolder<Option, ItemOptionBinding>(view) {

    private var mData: Option? = null


    override fun bindData(contentData: Option?) {
        mData = contentData
        binding?.option = contentData
        binding?.chkSelected?.isChecked = isSelected()
        binding?.chkSelected?.visibility = if (getSelectionMode() != BaseAdapter.Mode.IDLE) View.VISIBLE else View.GONE
    }
}