package com.apcc.emma.ui.file.local

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apcc.base.adapter.BaseAdapter
import com.apcc.base.fragment.BaseFragment
import com.apcc.emma.R
import com.apcc.emma.adapter.file.FileAdapter
import com.apcc.emma.databinding.FolderManagerFragmentBinding
import com.apcc.emma.ui.file.FileActivity
import com.apcc.emma.ui.file.FileCallback
import com.apcc.framework.AppManager
import com.apcc.framework.CacheManager
import com.apcc.utils.FileHelper
import com.apcc.utils.FileType
import com.apcc.utils.LayoutType
import com.apcc.view.XInput
import com.apcc.view.XToast


class FolderManagerFragment : BaseFragment<FolderManagerFragmentBinding, FolderManagerViewModel>(), Action {
    override val resourceLayoutId: Int
        get() = R.layout.folder_manager_fragment

    private var adapter = FileAdapter()
    var mFileCallback:FileCallback?=null
    private var mniBackFolder: MenuItem?=null
    private var mniCheck: MenuItem?=null
    private var mniRemove: MenuItem?=null
    private var mniCancel: MenuItem?=null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FileCallback){
            mFileCallback = context
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_folder_list, menu)
        mniBackFolder = menu.findItem(R.id.mniBackFolder)
        mniCheck = menu.findItem(R.id.mniCheck)
        mniCancel = menu.findItem(R.id.mniCancel)
        mniRemove = menu.findItem(R.id.mniRemove)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onHandleOptionsMenuSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mniBackFolder -> {
                backFolder()
                return true
            }
            R.id.mniCancel -> {
                cancelCheck()
                return true
            }
            R.id.mniCheck -> {
                toggleAll()
                return true
            }
            R.id.mniRemove -> {
                removeFiles()
                return true
            }
            R.id.mniLinear -> {
                setLinearViewMode()
            }
            R.id.mniGrid2 -> {
                setGridViewMode(2)
            }
            R.id.mniGrid3 -> {
                setGridViewMode(3)
            }
            R.id.mniGrid4 -> {
                setGridViewMode(4)
            }
            R.id.mniGrid5 -> {
                setGridViewMode(5)
            }
            R.id.mniGrid6 -> {
                setGridViewMode(6)
            }

            R.id.mniIgnoreFilter -> {
                updateFilter(FileType.UNKNOW)
            }
            R.id.mniFilterImage -> {
                updateFilter(FileType.IMAGE)
            }
            R.id.mniFilterMusic -> {
                updateFilter(FileType.SOUND)
            }
            R.id.mniFilterVideo -> {
                updateFilter(FileType.VIDEO)
            }
            R.id.mniFilterDocument -> {
                updateFilter(FileType.DOCUMENT)
            }
        }
        return false
    }

    override fun onInitView(root: View?) {
        screenName = getString(R.string.title_fileExplorer)
        binding.action = this
        binding.viewModel = viewModel

        setAdapterLayout(CacheManager.getViewLayoutType(AppManager.instance))
        binding.revFile.adapter = adapter
    }

    override fun subscribeUi() {
        binding.ipSearch.setListener(object : XInput.Listener {
            override fun onImeActionHandled(v: View, actionID: Int): Boolean {
                searchInList()
                return super.onImeActionHandled(v, actionID)
            }

            override fun onRightImageClick(v: View): Boolean {
                searchInList()
                return true
            }
        })

        adapter.addListener(object : BaseAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int): Boolean {
                itemSelected(position)
                return true
            }
        })

        adapter.addListener(object : BaseAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View?, position: Int):Boolean {
                startMultiCheck(position)
                return true
            }
        })

        viewModel.isWaiting.observe(viewLifecycleOwner) { isShow ->
            showProgress(isShow)
        }

        viewModel.alertMsg.observe(viewLifecycleOwner) { msg ->
            XToast.showErrorInfo(binding.ctlParent, msg)
        }

        viewModel.resultsGetFile.observe(viewLifecycleOwner) { response ->
            adapter.setData(response)

        }

        viewModel.focusPath.observe(viewLifecycleOwner){path->
            val parentPath = if (!TextUtils.isEmpty(path)) FileHelper.getParentFolder(path, "") else ""
            mniBackFolder?.isVisible = !TextUtils.isEmpty(parentPath)
        }
    }

    override fun allowToBack(): Boolean {
        if (adapter.getSelectionMode() == BaseAdapter.Mode.MULTI){
            cancelCheck()
            return false
        }
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////
    ////////////////////////////////////////////////////////////////////////////////

    private fun setAdapterLayout(layoutType: Int){
        when(layoutType){
            LayoutType.GRID_2 -> {
                setGridViewMode(2)
            }
            LayoutType.GRID_3 -> {
                setGridViewMode(3)
            }
            LayoutType.GRID_4 -> {
                setGridViewMode(4)
            }
            LayoutType.GRID_5 -> {
                setGridViewMode(5)
            }
            LayoutType.GRID_6 -> {
                setGridViewMode(6)
            }
            //LayoutType.LINEAR ->{}
            else ->{ setLinearViewMode() }
        }
    }

    private fun setLinearViewMode(){
        if (binding.revFile.layoutManager !is LinearLayoutManager || binding.revFile.layoutManager is GridLayoutManager){
            val linearLayoutManager = LinearLayoutManager(context)
            binding.revFile.layoutManager = linearLayoutManager
            CacheManager.saveViewLayoutType(AppManager.instance, LayoutType.LINEAR)
            adapter.setLayoutType(LayoutType.LINEAR)
        }
    }
    private fun setGridViewMode(columns: Int){
        val layoutManager = binding.revFile.layoutManager

        if (layoutManager is GridLayoutManager){
            if (layoutManager.spanCount != columns){
                layoutManager.spanCount = columns
            }
        }else{
            val mLayoutManager = GridLayoutManager(context, columns)
            binding.revFile.layoutManager = mLayoutManager
        }
        CacheManager.saveViewLayoutType(AppManager.instance, columns)
        adapter.setLayoutType(LayoutType.GRID_2)
    }

    private fun removeFiles(){
//        val fileIDs:MutableList<String> = ArrayList()
//        for (i in adapter.getSelectedPositions()){
//            val file = adapter.getData(i)
//            if (file != null)
//                fileIDs.add(file.fileAppID)
//        }
//        viewModel.removeFiles(fileIDs)
//        // clear at view
//        adapter.removeSelectedData()
    }

    private fun toggleAll(){
        if (!adapter.isSelectedAll())
            adapter.selectAll()
        else
            adapter.clearSelection()
    }

    private fun backFolder(){
        viewModel.focusPath.value?.let {path->
            val parentPath = if (!TextUtils.isEmpty(path)) FileHelper.getParentFolder(path, "") else ""
            if (!TextUtils.isEmpty(parentPath))
                viewModel.getFiles(folder = parentPath)
        }
    }

    private fun cancelCheck(){
        mniCancel?.isVisible = false
        mniCheck?.isVisible = false
        mniRemove?.isVisible = false
        adapter.setSelectionMode(BaseAdapter.Mode.IDLE)
        adapter.clearSelection() // clear selected
    }

    private fun startMultiCheck(position: Int){
        mniRemove?.isVisible = true
        mniCheck?.isVisible = true
        mniCancel?.isVisible = true
        adapter.setSelectionMode(BaseAdapter.Mode.MULTI)
        adapter.clearSelection(false) // clear selected
        adapter.addSelected(position)
    }

    private fun itemSelected(position: Int){
        if (adapter.getSelectionMode() == BaseAdapter.Mode.IDLE){
            adapter.getData(position)?.let {
                if (it.fileType == FileType.FOLDER){
                    viewModel.getFiles(folder = it.path!!)
                }else if (it.fileType == FileType.VIDEO){
//                    val actVideo = Intent(context, VideoPlayerActivity::class.java)
//                    actVideo.putExtra(FileActivity.EXTRA_FILE_OBJ, it)
//                    startActivity(actVideo)
                }else if (it.fileType == FileType.SOUND){
//                    val actMusic = Intent(context, MusicPlayerActivity::class.java)
//                    startActivity(actMusic)
                }else {
                    mFileCallback?.transFileDetail(this::class.java.name, it)
                }
            }
        }
    }

    private fun searchInList(){
        adapter.filter(binding.ipSearch.getText())
    }

    private fun updateFilter(filterType: Int){
       // asdasd
    }

}
