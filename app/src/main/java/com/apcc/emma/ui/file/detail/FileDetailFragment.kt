package com.apcc.emma.ui.file.detail

import android.content.Context
import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.FileDetailFragmentBinding
import com.apcc.data.FileApp
import com.apcc.emma.ui.file.FileActivity.Companion.EXTRA_FILE_OBJ
import com.apcc.emma.ui.file.FileCallback
import com.apcc.base.fragment.BaseFragment
import com.apcc.utils.FileType

class FileDetailFragment : BaseFragment<FileDetailFragmentBinding, FileDetailViewModel>(), Action {
    override val resourceLayoutId: Int
        get() = R.layout.file_detail_fragment

    var mFileCallback:FileCallback?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FileCallback){
            mFileCallback = context
        }
    }

    override fun onInitView(root: View?) {
        screenName = getString(R.string.title_fileDetail)
        binding.action = this

        binding.viewModel = viewModel

        val fileApp:FileApp? = arguments?.getParcelable(EXTRA_FILE_OBJ)
        viewModel.showFile(fileApp)
    }

    override fun subscribeUi() {
        viewModel.isWaiting.observe(viewLifecycleOwner) { isShow ->
            showProgress(isShow)
        }

        viewModel.resultsRemoveFile.observe(viewLifecycleOwner) { response ->
            viewModel.handlerRemoveFiles(response)
        }


    }

    ////////////////////////////////////////////////////////////////////////////////
    ////
    ////////////////////////////////////////////////////////////////////////////////

    override fun removeFile(){
    }
    override fun editFile(){

    }
}
