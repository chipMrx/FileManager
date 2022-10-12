package com.apcc.emma.ui.dialog.order

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogOrderBinding
import com.apcc.emma.adapter.option.OptionAdapter
import com.apcc.data.Option
import com.apcc.utils.Util
import com.apcc.base.dialog.BaseDialogVM

class OrderDialog : BaseDialogVM<DialogOrderBinding, OrderViewModel>(), Action{
    override val resourceLayoutId: Int
        get() = R.layout.dialog_order

    override val TAG: String
        get() = "OrderDialog"

    companion object {
        private const val EXTRA_TITLE = "extraTitle"
        private const val EXTRA_SELECTED = "extraSelected"
        private const val EXTRA_LIMIT_PICK = "extraLimitPick"
        private const val EXTRA_OPTION_TYPE = "extraOptionType"

        /** 0: no limit
         *  1: just one
         *  2: .....*/
        fun newInstance(
            title: String? = null,
            selectedIDs: ArrayList<String>? = null,
            limitPick: Int = 1,
            optionType: String = "",
            callback: Callback? = null
        ): OrderDialog {
            val dialog = OrderDialog()
            val bundle = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putStringArrayList(EXTRA_SELECTED, selectedIDs)
                putInt(EXTRA_LIMIT_PICK, limitPick)
                putString(EXTRA_OPTION_TYPE, optionType)
            }
            dialog.arguments = bundle
            dialog.mCallback = callback
            return dialog
        }
    }


    private var adapter = OptionAdapter()
    private var mCallback: Callback? = null

    override fun extraData() {
        arguments?.let {
            viewModel.mTitle.set(it.getString(EXTRA_TITLE, ""))
            viewModel.mLimitPicked.set(it.getInt(EXTRA_LIMIT_PICK, 1))
            viewModel.mOptionSelectedIDs = it.getStringArrayList(EXTRA_SELECTED)
            viewModel.mOptionType = it.getString(EXTRA_OPTION_TYPE) ?: ""
        }
    }

    override fun onInitView(root: View?) {
        binding.viewModel = viewModel
        binding.action = this

        val mLayoutManager = LinearLayoutManager(context)
        binding.revOrderPending.layoutManager = mLayoutManager
        binding.revOrderPending.adapter = adapter
    }

    override fun subscribeUi() {

    }

    override fun configDialogAttributes(attributes: WindowManager.LayoutParams) {
        attributes.gravity = Gravity.CENTER
        attributes.width = resources.displayMetrics.widthPixels * Util.getInt(requireContext(), R.integer.dialogWidthPercentBig)/100 // make dialog width = 65% screen size
        attributes.height = resources.displayMetrics.heightPixels * Util.getInt(requireContext(), R.integer.dialogWidthPercentBig) / 100
    }


    override fun onPickedDoneClick() {

    }


    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////
    interface Callback {
        fun onPickOptionsDone(options: List<Option>)
    }


}