package com.apcc.emma.ui.dialog.previewImage

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.ObservableField
import com.apcc.emma.R
import com.apcc.data.Option
import com.apcc.emma.databinding.DialogPreviewImageBinding
import com.apcc.base.dialog.BaseDialog

class ImagePreviewDialog:BaseDialog<DialogPreviewImageBinding>() {
    override val resourceLayoutId: Int
        get() = R.layout.dialog_preview_image

    override val TAG: String
        get() = "ImagePreviewDialog"

    companion object{
        private const val EXTRA_TITLE = "extraTitle"
        private const val EXTRA_VAL = "extraVal"
        private const val EXTRA_FOCUS_INDEX = "extraFocusIndex"

        fun newInstance(title:String? = null, images:ArrayList<Option>?=null , focus:Int = 0):ImagePreviewDialog{
            val dialog = ImagePreviewDialog()
            val bundle = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putParcelableArrayList(EXTRA_VAL, images)
                putInt(EXTRA_FOCUS_INDEX, focus)
            }
            dialog.arguments = bundle
            return dialog
        }
    }

    var mTitle:ObservableField<String> = ObservableField("")
    private var mImages:MutableList<Option> = ArrayList()
    private lateinit var mAdapter: ImagePagerAdapter


    override fun extraData() {
        arguments?.let {
            mTitle.set(it.getString(EXTRA_TITLE, ""))
            mImages = it.getParcelableArrayList(EXTRA_VAL)?:ArrayList()
        }
    }

    override fun onInitView(root: View?) {
    }

    override fun subscribeUi() {
        binding.dialog = this
        mAdapter = ImagePagerAdapter(childFragmentManager,mImages)
        binding.vpSlide.adapter = mAdapter
        binding.tabIndicator.setupWithViewPager(binding.vpSlide)
    }

    override fun configDialogAttributes(attributes: WindowManager.LayoutParams){
        attributes.gravity = Gravity.CENTER
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
    }


    fun viewDone(){
        try {
            //mCallback?.onInputDone(edtInput.text.toString())
            dismissAllowingStateLoss()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////



}