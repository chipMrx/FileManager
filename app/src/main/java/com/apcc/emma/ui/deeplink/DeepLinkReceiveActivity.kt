package com.apcc.emma.ui.deeplink

import android.net.Uri

import com.apcc.emma.R
import com.apcc.framework.AppManager
import com.apcc.base.activity.BaseActivity
import com.apcc.utils.Logger

class DeepLinkReceiveActivity : BaseActivity() {

    override val resourceLayoutId: Int
        get() = R.layout.deep_link_activity

    override fun onInit() {
        AppManager.instance?.flagValid = true // flag to know app is running

        val action: String? = intent?.action
        val data: Uri? = intent?.data

        Logger.d( " action = $action, data = ${data.toString()}")
    }
}
