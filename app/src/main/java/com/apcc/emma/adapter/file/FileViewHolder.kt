package com.apcc.emma.adapter.file

import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.ItemFileBinding
import com.apcc.data.FileApp
import com.apcc.utils.FileType
import com.apcc.base.adapter.BaseAdapter
import com.apcc.base.adapter.BaseViewHolder


class FileViewHolder(view: View)
    : BaseViewHolder<FileApp, ItemFileBinding>(view) {


    override fun bindData(contentData: FileApp?) {
        //binding?.txtTitle?.text = contentData?.getFileSizeDisplay()?:""
        binding?.txtTitle?.text = contentData?.getTitle()?:""
        binding?.txtDescription?.text = contentData?.getDate()?:""
        binding?.chkSelected?.isChecked = isSelected()
        binding?.chkSelected?.visibility = if (getSelectionMode() == BaseAdapter.Mode.MULTI) View.VISIBLE else View.GONE
        if (contentData != null){
            when(contentData.fileType){
                FileType.IMAGE -> {
                    binding?.icPreview?.loadByPath(contentData.path, R.drawable.ic_picture)
                }
                FileType.SOUND -> {
                    binding?.icPreview?.setImageResource(R.drawable.ic_music)
                }
                FileType.DOCUMENT -> {
                    binding?.icPreview?.setImageResource(R.drawable.ic_doc)
                }
                FileType.VIDEO -> {
                    binding?.icPreview?.setImageResource(R.drawable.ic_video)
                }
                FileType.FOLDER -> {
                    binding?.icPreview?.setImageResource(R.drawable.ic_folder)
                }
                else -> {
                    binding?.icPreview?.setImageResource(R.drawable.ic_app)
                }
            }
        }else{
            binding?.icPreview?.setImageResource(R.drawable.ic_app)
        }
    }
}