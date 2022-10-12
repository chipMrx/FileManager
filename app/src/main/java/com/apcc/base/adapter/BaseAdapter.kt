package com.apcc.base.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import com.apcc.utils.DataHelper
import com.apcc.utils.LayoutType
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseAdapter<DT, HD : BaseViewHolder<DT,*>> : RecyclerView.Adapter<HD>(), AdapterCallback{
    protected abstract fun getResourceLayoutId(viewType: Int): Int

    private var filterText:CharSequence? = null
    private val mOriginData:MutableList<DT?>  = ArrayList()
    private val mDisplayData:MutableList<DT?>  = ArrayList()
    private val mBoundViewHolders:MutableSet<HD>  = HashSet()
    private val mSelectArr:MutableSet<Int> = HashSet()
    private val mItemClickListeners:MutableSet<OnItemClickListener> = HashSet()
    private val mItemLongClickListeners:MutableSet<OnItemLongClickListener> = HashSet()
    private val mFilterListeners:MutableList<OnFilterListener> = ArrayList()
    private var mInflater:LayoutInflater?=null
    private var selectionMode = Mode.IDLE // no selection
    protected var mLayoutType = LayoutType.LINEAR

    private var mItemFilter:Filter? = null


    abstract fun getViewHolder(viewRoot:View, viewType: Int):HD
    abstract protected fun filterEquals(constraint: String? = filterText?.toString(), contentData:DT?): Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HD {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.context)
        }
        val viewRoot = mInflater!!.inflate(getResourceLayoutId(viewType), parent, false)
        return getViewHolder(viewRoot, viewType)
    }

    override fun onBindViewHolder(holder: HD, position: Int) {
        if (holder.isRecyclable){
            holder.bindingViewHolder(getData(position), this)
            mBoundViewHolders.add(holder)
        }
    }

    override fun onViewRecycled(holder: HD) {
        mBoundViewHolders.remove(holder)
    }

    override fun getItemCount(): Int {
        return mDisplayData.size
    }

    override fun onClick(view: View?, position: Int) {
        toggleSelection(position)
        for (listener in mItemClickListeners){
            if (listener.onItemClick(view, position))
                return
        }
    }

    override fun onLongClick(view: View?, position: Int): Boolean {
        for (listener in mItemLongClickListeners){
            if (listener.onItemLongClick(view, position))
                return true
        }
        return false
    }
    //////////////////////////////////////////////
    ///// data update
    //////////////////////////////////////////////

    fun getData(position: Int):DT?{
        if (mDisplayData.size > position)
            return mDisplayData[position]
        return null
    }

    fun setData(datas:List<DT?>?){
        mOriginData.clear()
        datas?.let {
            mOriginData.addAll(datas)
        }
        if (hasFilter()){
            filter()
        }else{
            updateDisplay()
        }
    }

    /**
     * default add to last
     * ignore check filter
     * return position; if -1 // don't show on current list => don't notify
     */
    fun addData(data:DT, position:Int=-1, continueFilter:Boolean = false):Int{
        if (position == -1){ // add to last
            mOriginData.add(data)
            if (!continueFilter || filterEquals(contentData = data)){
                mDisplayData.add(data)
                return mDisplayData.size - 1
            }
        }else{
            // add to origin
            if (position < mOriginData.size)
                mOriginData.add(position, data)
            else
                mOriginData.add(data)
            // add to display
            if (!continueFilter || filterEquals(contentData = data)){
                if (position < mDisplayData.size) {
                    mDisplayData.add(position, data)
                    return position
                }
                else {
                    mDisplayData.add(data)
                    return mDisplayData.size - 1
                }
            }
        }
        return -1
    }

    fun removeSelectedData(){
        val selectedList = getSelectedPositions()
        for (index in selectedList.sortedDescending()){
            val data = mDisplayData.removeAt(index)
            data?.let {
                mOriginData.remove(it)
            }
        }
        clearSelection()
    }

    //////////////////////////////////////////////
    ///// filter
    //////////////////////////////////////////////

    fun filter(constraint: CharSequence? = filterText){
        if (mItemFilter == null)
            mItemFilter = ItemFilter()
        filterText = constraint
        mItemFilter?.filter(filterText)
    }

    fun hasFilter():Boolean{
        return !TextUtils.isEmpty(filterText)
    }

    fun isNewFilter(constraint: CharSequence?):Boolean{
        return DataHelper.compareString(constraint, filterText, true) == 0
    }

    private fun notifyFilterResult(size: Int){
        for (listener in mFilterListeners){
            listener.onUpdateFilterView(size)
        }
    }

    //////////////////////////////////////////////
    ///// selection
    //////////////////////////////////////////////
    override fun getSelectionMode(): Int {
        return selectionMode
    }

    fun setSelectionMode(@Mode selectionMode:Int){
        this.selectionMode = selectionMode
    }
    //////////////////////////////////////////

    fun isSelectedAll():Boolean{
        if (selectionMode == Mode.IDLE)
            return false
        return mSelectArr.isNotEmpty() && mSelectArr.size == mDisplayData.size
    }

    fun hasSelected():Boolean{
        if (selectionMode == Mode.IDLE)
            return false
        return mSelectArr.isNotEmpty()
    }

    override fun isSelected(position:Int):Boolean{
        if (selectionMode == Mode.IDLE)
            return false
        return mSelectArr.contains(position)
    }

    fun setSelected(position: Int, requestNotifyData:Boolean = true){
        if (selectionMode == Mode.IDLE)
            return
        clearSelection(false)
        if (mSelectArr.add(position) && requestNotifyData)
            notifyItemChanged(position)
    }

    fun addSelected(position:Int, requestNotifyData:Boolean = true){
        if (selectionMode == Mode.IDLE)
            return
        if (selectionMode == Mode.SINGLE)
            setSelected(position, requestNotifyData)
        else if (mSelectArr.add(position) && requestNotifyData)
            notifyItemChanged(position)
    }

    fun selectAll(){
        if (selectionMode == Mode.IDLE)
            return
        if (mDisplayData.size == 0)
            return
        for (i in 0 until mDisplayData.size){
            mSelectArr.add(i)
        }
        notifyDataSetChanged()
    }

    fun removeSelected(position:Int){
        if (mSelectArr.remove(position))
            notifyItemChanged(position)
    }

    fun clearSelection(requestNotifyData:Boolean = true){
        mSelectArr.clear()
        if (requestNotifyData)
            notifyDataSetChanged()
    }

    fun toggleSelection(position: Int){
        if (mSelectArr.contains(position))
            removeSelected(position)
        else{
            addSelected(position)
        }
    }
    fun getSelectedPositions():List<Int>{
        return ArrayList(mSelectArr)
    }

    //////////////////////////////////////////////
    ///// layout type
    //////////////////////////////////////////////

    fun setLayoutType(@LayoutType layoutType: Int){
        if (mLayoutType != layoutType){
            mLayoutType = layoutType
            if (hasFilter())
                filter()
            else
                updateDisplay()
        }
    }

    fun getLayoutType() = mLayoutType

    //////////////////////////////////////////////
    ///// listener
    //////////////////////////////////////////////
    fun addListener(listener: Any){
        when(listener){
            is OnItemClickListener->{ mItemClickListeners.add(listener) }
            is OnItemLongClickListener->{mItemLongClickListeners.add(listener)}
            is OnFilterListener->{mFilterListeners.add(listener)}
        }
    }
    fun removeListener(listener: Any){
        when(listener){
            is OnItemClickListener->{ mItemClickListeners.remove(listener) }
            is OnItemLongClickListener->{mItemLongClickListeners.remove(listener)}
            is OnFilterListener->{mFilterListeners.remove(listener)}
        }
    }

    fun clearListener(){
        mItemClickListeners.clear()
        mItemLongClickListeners.clear()
        mFilterListeners.clear()
    }

    //////////////////////////////////////////////
    ///// function support
    //////////////////////////////////////////////
    private fun updateDisplay(datas:List<DT?> = mOriginData){
        discardBoundViewHolders()
        clearSelection(false)
        mDisplayData.clear()
        mDisplayData.addAll(datas)
        notifyDataSetChanged()
    }

    private fun discardBoundViewHolders(){
        mBoundViewHolders.clear()
    }

    //////////////////////////////////////////////
    ///// outer class
    //////////////////////////////////////////////

    interface OnItemClickListener{
        fun onItemClick(view: View?, position: Int): Boolean
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(view: View?, position: Int): Boolean
    }

    interface OnFilterListener{
        fun onUpdateFilterView(size:Int)
    }

    //////////////////////////////////////////////
    ///// inner class
    //////////////////////////////////////////////
    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterString = constraint?.toString()?:""
            val results = FilterResults()
            if (TextUtils.isEmpty(constraint)){
                results.values = mOriginData
                results.count = mOriginData.size
            }else{
                val filterList = ArrayList<DT?>()
                for (data in mOriginData){
                    if (filterEquals(filterString, data)){
                        filterList.add(data)
                    }
                }
                results.values = filterList
                results.count = filterList.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.values?.let {vals->
                updateDisplay(vals as List<DT?>)
                notifyFilterResult(vals.size)
            }
        }
    }


    //////////////////////////////////////////////
    ///// type
    //////////////////////////////////////////////

    @IntDef(
        Mode.IDLE,
        Mode.SINGLE,
        Mode.MULTI
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode {
        companion object {
            const val IDLE = 0
            const val SINGLE = 1
            const val MULTI = 2
        }
    }

    @IntDef(
        ViewType.ITEM,
        ViewType.HEADER
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ViewType {
        companion object {
            const val ITEM = 0
            const val HEADER = 1
        }
    }
}