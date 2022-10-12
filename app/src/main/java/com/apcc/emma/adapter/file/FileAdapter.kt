package com.apcc.emma.adapter.file

import android.view.View
import androidx.databinding.ViewDataBinding
import com.apcc.emma.R
import com.apcc.data.FileApp
import com.apcc.utils.DataHelper
import com.apcc.utils.LayoutType
import com.apcc.base.adapter.BaseAdapter
import com.apcc.base.adapter.BaseViewHolder

class FileAdapter : BaseAdapter<FileApp, BaseViewHolder<FileApp, ViewDataBinding>>(){
    override fun getResourceLayoutId(viewType: Int): Int {
        return when(viewType){
            LayoutType.LINEAR-> R.layout.item_file
            else-> R.layout.item_file_large
        }
    }

    override fun getViewHolder(viewRoot: View, viewType: Int): BaseViewHolder<FileApp, ViewDataBinding> {
        return (when(viewType){
            LayoutType.LINEAR-> FileViewHolder(viewRoot)
            else->FileViewHolderLarge(viewRoot)
        })as BaseViewHolder<FileApp, ViewDataBinding>
    }

    override fun getItemViewType(position: Int): Int {
        return mLayoutType
    }

    override fun filterEquals(constraint: String?, contentData: FileApp?): Boolean {
        if (constraint == null)
            return true
        if ( contentData == null)
            return false

        return DataHelper.supportContains(contentData.path, constraint)
                || DataHelper.supportContains(contentData.fileDescription, constraint)
    }

}