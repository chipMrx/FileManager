package com.apcc.base.fragment

import android.app.Application
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import com.apcc.base.activity.BaseActivity
import com.apcc.base.activity.BaseActivityResult
import com.apcc.utils.ViewHelper

abstract class AbsFragment : Fragment(){

    abstract val resourceLayoutId: Int
    var screenName: String? = ""

    abstract fun onInitView(root: View?)
    abstract fun subscribeUi()
    open fun enableHomeAsBackButton(): Boolean {
        return true
    }
    /**
     * return true => allow to back
     */
    open fun allowToBack():Boolean{
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        ViewHelper.updateMenuTint(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (onHandleOptionsMenuSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }



    protected val application: Application?
        get() = activity?.application

    open fun onHandleOptionsMenuSelected(item: MenuItem): Boolean {
        return false
    }

    fun startActivityForResult(intent:Intent, resultListener: BaseActivityResult.OnActivityResult?=null){
        if (activity is BaseActivity){
            (activity as BaseActivity).lunchForResult(intent, resultListener)
        }
    }
}