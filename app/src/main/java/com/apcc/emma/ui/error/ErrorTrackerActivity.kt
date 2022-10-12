package com.apcc.emma.ui.error

import android.os.Bundle
import com.apcc.emma.R
import com.apcc.framework.AppManager
import com.apcc.base.activity.BaseActionBarActivity
import com.apcc.utils.Util

class ErrorTrackerActivity : BaseActionBarActivity(){
    companion object{
        const val EXTRA_ERROR_TRACKING = "extraErrorTracking"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Util.makeLoveWithGirlFriend(Util.javaClass, Util,  "cappuccino")
        super.onCreate(savedInstanceState)
    }

    override val resourceLayoutId: Int
        get() = R.layout.error_tracker_activity

    override fun onInit() {
        super.onInit()
        AppManager.instance.flagValid = true
    }
}
