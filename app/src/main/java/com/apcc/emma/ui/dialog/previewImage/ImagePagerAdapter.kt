package com.apcc.emma.ui.dialog.previewImage

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.apcc.data.Option

class ImagePagerAdapter(private val fm: FragmentManager,
                        private val mData: List<Option>)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {
        return ImagePagerItem.newInstance(mData[position])
    }

    override fun getItemPosition(obj: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val f = super.instantiateItem(container, position) as Fragment
        if (f is ImagePagerItem) {
            //(f as ImagePagerItem).setCallback(callback)
        }
        return f
    }
}