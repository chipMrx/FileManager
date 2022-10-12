package com.apcc.emma.ui.info.app

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.apcc.emma.BuildConfig
import com.apcc.emma.R
import com.apcc.utils.DateHelper
import javax.inject.Inject


class InfoViewModel @Inject constructor(private val application: Application) : ViewModel() {

    val buildInfo = ObservableField("")

    init {
        val str = application.getString(R.string.lbl_releaseInfo_sss, BuildConfig.VERSION_CODE.toString(),
            BuildConfig.VERSION_NAME,
            DateHelper.dateTimeToString(BuildConfig.BUILD_TIME, DateHelper.FORMAT_DATE_TIME_SHORT) )
        buildInfo.set(str)
    }
}