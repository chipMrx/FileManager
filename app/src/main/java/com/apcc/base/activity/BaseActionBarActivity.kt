package com.apcc.base.activity

import android.text.TextUtils
import com.apcc.emma.R


abstract class BaseActionBarActivity : BaseActivity() {

    //////////////////////////////////
    //// suport
    ////////////////////////////////////


    override fun onInit() {
        setUpActionBar()
    }


    private fun setUpActionBar(){
        supportActionBar?.let {acb->
            acb.setDisplayShowHomeEnabled(true)
            acb.setLogo(R.mipmap.ic_launcher_round)
            acb.setDisplayUseLogoEnabled(true)
            acb.setDisplayShowTitleEnabled(true) // title
            acb.setDisplayHomeAsUpEnabled(false) // back button
        }
    }

    open fun enableHomeAsBackButton(enable: Boolean) {
        supportActionBar?.let {acb->
            acb.setDisplayShowTitleEnabled(true)
            acb.setDisplayShowHomeEnabled(!enable)
            acb.setDisplayHomeAsUpEnabled(enable)
            if (!enable){
                acb.setLogo(R.mipmap.ic_launcher_round)
            }
            acb.setDisplayUseLogoEnabled(!enable)
        }

    }

    open fun updateTitle(title: String?) {
        if (!TextUtils.isEmpty(title)) {
            supportActionBar?.title = title
        }
    }
}