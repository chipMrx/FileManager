package com.apcc.emma.ui.dialog.option

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogPickOptionBinding
import com.apcc.data.Option
import com.apcc.utils.Util
import com.apcc.base.dialog.BaseDialogVM
import com.apcc.emma.adapter.option.OptionAdapter
import com.apcc.base.adapter.BaseAdapter
import com.apcc.view.XInput

class OptionDialog : BaseDialogVM<DialogPickOptionBinding, OptionViewModel>(), Action{
    override val resourceLayoutId: Int
        get() = R.layout.dialog_pick_option

    override val TAG: String
        get() = "OptionDialog"
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
        ): OptionDialog {
            val dialog = OptionDialog()
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
        binding.revOption.layoutManager = mLayoutManager
        binding.revOption.adapter = adapter
    }

    override fun subscribeUi() {
        adapter.setSelectionMode(if (viewModel.mLimitPicked.get() != 1)
            BaseAdapter.Mode.MULTI
        else
            BaseAdapter.Mode.SINGLE)

        binding.ipNewOption.setListener(object : XInput.Listener {
            override fun onRightImageClick(v: View): Boolean {
                if (viewModel.saveOption(binding.ipNewOption.getText())) {
                    viewModel.addItemSelected(binding.ipNewOption.getText())
                }
                return true // auto clear text
            }

            override fun onContentChanged(s: String) {
                searchInList(s)
            }
        })

        adapter.addListener(object : BaseAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int): Boolean {
                clickItemSelected(position)
                return true
            }
        })

        viewModel.getOptions().observe(viewLifecycleOwner) { options ->
            adapter.setData(options)
            fillSelectedItems()
        }
    }

    override fun configDialogAttributes(attributes: WindowManager.LayoutParams) {
        super.configDialogAttributes(attributes)
        attributes.height = resources.displayMetrics.heightPixels * Util.getInt(
            requireContext(),
            R.integer.dialogWidthPercent
        ) / 100 // make dialog height = 65% screen size
    }


    override fun onPickedDoneClick() {
        val options: MutableList<Option> = ArrayList()
        for (i in adapter.getSelectedPositions()) {
            val option = adapter.getData(i)
            if (option != null)
                options.add(option)
        }
        doPickedDone(options)
    }


    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////

    private fun clickItemSelected(position: Int) {
        if (adapter.getSelectionMode() != BaseAdapter.Mode.MULTI) {
            doPickedDone(adapter.getData(position))
        }
    }

    private fun fillSelectedItems() {
        viewModel.mOptionSelectedIDs?.let { ids ->
            if (ids.isNotEmpty()) {
                for (id in ids) {
                    val index = indexOfItem(id)
                    if (index >= 0)
                        adapter.addSelected(index, false)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun indexOfItem(id: String): Int {
        if (adapter.itemCount > 0) {
            for (i in 0 until adapter.itemCount) {
                if (adapter.getData(i)?.optionID?.equals(id) == true) {
                    return i
                }
            }
        }
        return -1
    }

    private fun searchInList(searchText: String) {
        adapter.filter(searchText)
    }

    private fun doPickedDone(option: Option?) {
        option?.let {
            val options: MutableList<Option> = ArrayList()
            options.add(option)
            doPickedDone(options)
        }
    }

    private fun doPickedDone(options: List<Option>) {
        try {
            mCallback?.onPickOptionsDone(options)

            dismissAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////
    interface Callback {
        fun onPickOptionsDone(options: List<Option>)
    }


}