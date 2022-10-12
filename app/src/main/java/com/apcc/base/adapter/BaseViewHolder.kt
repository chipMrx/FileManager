package com.apcc.base.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<DT, BD: ViewDataBinding>(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener, View.OnLongClickListener {
    var binding:BD?=null
    var contentData:DT?=null
    var mAdapterCallback: AdapterCallback?= null

    init {
        binding = DataBindingUtil.bind(itemView)
    }

    fun bindingViewHolder(contentData:DT?, adapterCallback:AdapterCallback){
        this.contentData = contentData
        this.mAdapterCallback = adapterCallback

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        refreshView()
        bindData(contentData)
    }

    abstract fun bindData(contentData:DT?)

    ////////////////////////////////////////////////////////////
    /// support function
    ////////////////////////////////////////////////////////////
    private fun refreshView(){
        itemView.isSelected = isSelected()
    }

    protected fun isSelected()  = mAdapterCallback?.isSelected(adapterPosition) == true

    protected fun getSelectionMode() =  mAdapterCallback?.getSelectionMode()?:BaseAdapter.Mode.IDLE
    ////////////////////////////////////////////////////////////
    /// listener
    ////////////////////////////////////////////////////////////

    override fun onClick(v: View?) {
        mAdapterCallback?.onClick(v, adapterPosition)
    }

    override fun onLongClick(v: View?): Boolean {
        mAdapterCallback?.let {
            return it.onLongClick(v, adapterPosition)
        }
        return false
    }
}