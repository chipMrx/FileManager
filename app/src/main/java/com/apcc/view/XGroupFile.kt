package com.apcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.apcc.emma.R
import com.apcc.data.FileApp
import kotlin.math.min

@SuppressLint("AppCompatCustomView")
class XGroupFile: ConstraintLayout, ViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs){
        parseAttributes(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr){
        parseAttributes(context, attrs)
    }

    companion object{

    }

    lateinit var txtLabel:XTextView
    lateinit var txtMore:XTextView
    lateinit var btnAddFile:XButton
    lateinit var ic1:XIcon
    lateinit var ic2:XIcon
    lateinit var ic3:XIcon
    lateinit var ic4:XIcon
    lateinit var ic5:XIcon
    lateinit var ic6:XIcon
    lateinit var ic7:XIcon
    lateinit var ic8:XIcon
    lateinit var ic9:XIcon
    lateinit var ic10:XIcon
    lateinit var ic11:XIcon
    lateinit var ic12:XIcon
    private var mListener:Listener? = null

    private var mFileList:MutableList<FileApp> = ArrayList()
    private var mEnable = true

    private var mLabel: String? = ""

    //////////////////////////////////////////////////
    /// system
    //////////////////////////////////////////////////

    override val mStyleableID: IntArray = R.styleable.XGroupFile
    override val mLayoutID: Int = R.layout.view_group_file
    override val mViewGroup: ViewGroup = this

    override fun extraTypes(typedArray: TypedArray) {
        mEnable = typedArray.getBoolean(R.styleable.XGroupFile_android_enabled, true)
        mLabel = typedArray.getString(R.styleable.XGroupFile_android_label)
    }

    override fun initView(root: View) {
        txtMore = root.findViewById(R.id.txtMore)
        txtLabel = root.findViewById(R.id.lblLabel)
        btnAddFile = root.findViewById(R.id.btnAddFile)
        ic1 = root.findViewById(R.id.ic1)
        ic2 = root.findViewById(R.id.ic2)
        ic3 = root.findViewById(R.id.ic3)
        ic4 = root.findViewById(R.id.ic4)
        ic5 = root.findViewById(R.id.ic5)
        ic6 = root.findViewById(R.id.ic6)
        ic7 = root.findViewById(R.id.ic7)
        ic8 = root.findViewById(R.id.ic8)
        ic9 = root.findViewById(R.id.ic9)
        ic10 = root.findViewById(R.id.ic10)
        ic11 = root.findViewById(R.id.ic11)
        ic12 = root.findViewById(R.id.ic12)

        txtMore.setOnClickListener { mListener?.onFileClick(-1) }
        btnAddFile.setOnClickListener { mListener?.onAddFileClick() }
        ic1.setOnClickListener { mListener?.onFileClick(0) }
        ic2.setOnClickListener { mListener?.onFileClick(1) }
        ic3.setOnClickListener { mListener?.onFileClick(2) }
        ic4.setOnClickListener { mListener?.onFileClick(3) }
        ic5.setOnClickListener { mListener?.onFileClick(4) }
        ic6.setOnClickListener { mListener?.onFileClick(5) }
        ic7.setOnClickListener { mListener?.onFileClick(6) }
        ic8.setOnClickListener { mListener?.onFileClick(7) }
        ic9.setOnClickListener { mListener?.onFileClick(8) }
        ic10.setOnClickListener { mListener?.onFileClick(9) }
        ic11.setOnClickListener { mListener?.onFileClick(10) }
        ic12.setOnClickListener { mListener?.onFileClick(11) }
    }

    override fun updateView() {
        setEnable(mEnable)
        setLabel(mLabel)
    }

    fun setListener(listener: Listener){
        mListener = listener
    }

    //////////////////////////////////////////////////
    /// function support
    //////////////////////////////////////////////////

    fun setLabel(label: String?) {
        mLabel = if (TextUtils.isEmpty(label)) "" else label
        txtLabel.text = mLabel
        txtLabel.visibility = if (TextUtils.isEmpty(label)) GONE else VISIBLE
    }


    fun setFiles(list: List<FileApp>){
        mFileList.clear()
        mFileList.addAll(list)
        autoShow()
    }

    fun getFiles():MutableList<FileApp>{
        return mFileList
    }

    /**
     * @param index: default is -1, that mean insert to last
     */
    fun addFile(fileApp: FileApp?, index: Int = -1){
        fileApp?.let {
            if (index == -1)
                mFileList.add(fileApp)
            else
                mFileList.add(index, fileApp)
            autoShow()
        }
    }

    /**
     * @param index: default is -1, that mean insert to last
     */
    fun addFile(fileApps: Collection<FileApp>?, index: Int = -1){
        fileApps?.let {
            if (index == -1)
                mFileList.addAll(fileApps)
            else
                mFileList.addAll(index, fileApps)
            autoShow()
        }
    }

    fun getFileIndex(index: Int):FileApp?{
        if (mFileList.size > index)
            return mFileList[index]
        return null
    }

    fun removeFileApp(fileApp: FileApp?){
        fileApp?.let {
            mFileList.remove(fileApp)
            autoShow()
        }
    }

    fun removeFileApp(index:Int){
        if (index >=0 && index < mFileList.size){
            mFileList.removeAt(index)
            autoShow()
        }
    }

    fun removeFileApp(fileApps: Collection<FileApp>?){
        fileApps?.let {
            for (fileApp in it){
                mFileList.remove(fileApp)
            }
        }
        autoShow()
    }

    private fun autoShow(){
        // icAddFile is the first
        val canShow = min(min((width - btnAddFile.width) / ic1.width, 12),mFileList.size) // 12 total icon view

        showImage(ic1, 0 , canShow)
        showImage(ic2,1, canShow)
        showImage(ic3,2, canShow)
        showImage(ic4,3 , canShow)
        showImage(ic5,4 , canShow)
        showImage(ic6,5 , canShow)
        showImage(ic7,6 , canShow)
        showImage(ic8,7 , canShow)
        showImage(ic9,8 , canShow)
        showImage(ic10,9, canShow)
        showImage(ic11,10, canShow)
        showImage(ic12,11, canShow)
        txtMore.visibility = if (canShow < mFileList.size) VISIBLE else GONE
        txtMore.text = "${mFileList.size - canShow}+"
    }
    private fun showImage(ic:XIcon, index:Int, canShow:Int){
        ic.visibility = if (canShow > index) VISIBLE else GONE
        ic.loadByPath(getFileIndex(index)?.path, R.drawable.ic_img)
    }

    fun setEnable(enable:Boolean){
        mEnable = enable
        txtMore.isEnabled = enable
        btnAddFile.isEnabled = enable
        ic1.isEnabled = enable
        ic2.isEnabled = enable
        ic3.isEnabled = enable
        ic4.isEnabled = enable
        ic5.isEnabled = enable
        ic6.isEnabled = enable
        ic7.isEnabled = enable
        ic8.isEnabled = enable
        ic9.isEnabled = enable
        ic10.isEnabled = enable
        ic11.isEnabled = enable
        ic12.isEnabled = enable
    }

    //////////////////////////////////////////////////
    ///
    //////////////////////////////////////////////////
    interface Listener{
        fun onFileClick(index:Int)
        fun onAddFileClick()
    }

}