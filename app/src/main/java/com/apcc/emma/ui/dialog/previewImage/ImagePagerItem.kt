package com.apcc.emma.ui.dialog.previewImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.apcc.emma.R
import com.apcc.data.Option
import com.apcc.emma.databinding.ImageFragmentBinding

class ImagePagerItem : Fragment(){

    companion object{
        private const val EXTRA_VAL = "extraVal"

        fun newInstance(option:Option):ImagePagerItem{
            val fragment = ImagePagerItem()
            val bundle = Bundle().apply {
                putParcelable(EXTRA_VAL, option)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private var binding:ImageFragmentBinding?=null
    val mOption = ObservableField(Option())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {bundle->
            val option: Option? = bundle.getParcelable(EXTRA_VAL)
            mOption.set(option?:Option())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.image_fragment, container, false)
        binding = DataBindingUtil.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.dialog = this
    }
    /////////////////////////////////////////////
    /////
    ///////////////////////////////////////
}