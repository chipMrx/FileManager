package com.apcc.emma.adapter.option

import android.view.View
import com.apcc.emma.R
import com.apcc.data.Option
import com.apcc.utils.DataHelper
import com.apcc.base.adapter.BaseAdapter

class OptionAdapter : BaseAdapter<Option, OptionViewHolder>() {

    override fun getResourceLayoutId(viewType:Int) = R.layout.item_option

    override fun getViewHolder(viewRoot: View, viewType:Int): OptionViewHolder {
        return OptionViewHolder(viewRoot)
    }


    override fun filterEquals(constraint: String?, contentData: Option?): Boolean {
        if (constraint == null)
            return true
        if ( contentData == null)
            return false

        return DataHelper.supportContains(contentData.title, constraint)
                || DataHelper.supportContains(contentData.description, constraint)
                || DataHelper.supportContains(contentData.str1, constraint)
                || DataHelper.supportContains(contentData.str2, constraint)
                || DataHelper.supportContains(contentData.str3, constraint)
                || DataHelper.supportContains(contentData.str4, constraint)
                || DataHelper.supportContains(contentData.str5, constraint)
                || DataHelper.supportContains(contentData.int1.toString(), constraint)
                || DataHelper.supportContains(contentData.int2.toString(), constraint)
                || DataHelper.supportContains(contentData.int3.toString(), constraint)
                || DataHelper.supportContains(contentData.int4.toString(), constraint)
                || DataHelper.supportContains(contentData.int5.toString(), constraint)
    }

}