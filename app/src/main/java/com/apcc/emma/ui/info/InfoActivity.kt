package com.apcc.emma.ui.info

import android.content.Context
import android.content.Intent
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.apcc.emma.R
import com.apcc.base.activity.BaseActionBarActivity

class InfoActivity : BaseActionBarActivity(), InfoActivityCallback {
    companion object{
        private const val EXTRA_SHOW = "extraShow"
        const val SHOW_INFO = 0
        const val SHOW_TERM = 1
        const val SHOW_LICENSE = 2

        fun show(context: Context?, what:Int){
            if (context == null)
                return
            val intent = Intent(context, InfoActivity::class.java)
            intent.putExtra(EXTRA_SHOW, what)
            context.startActivity(intent)
        }
    }

    private var mShowWhat = SHOW_INFO

    override val resourceLayoutId: Int
        get() = R.layout.info_activity



    override fun onInit() {
        super.onInit()
        mNavController = findNavController(R.id.navHostInfo)
        intent?.let {
            mShowWhat = intent.getIntExtra(EXTRA_SHOW, SHOW_INFO)
        }
        defaultShowWhat()
    }

    ///////////////////////////////////////////////////
    //// action
    /////////////////////////////////////

    override fun showTerm(isExclusive:Boolean){
        mNavController?.navigate(R.id.navTerm, null, getOptionExclusive(isExclusive))
    }

    override fun showLicense(isExclusive: Boolean) {
        mNavController?.navigate(R.id.navLicense, null, getOptionExclusive(isExclusive))
    }

    override fun showInfo(isExclusive: Boolean) {
        mNavController?.navigate(R.id.navInfo, null, getOptionExclusive(isExclusive))
    }


    override fun popBack(className: String) {
        super.popBackStack(className)
    }

    ///////////////////////////////////////////////////
    //// support
    /////////////////////////////////////
    private fun getOptionExclusive(isExclusive: Boolean):NavOptions?{
        if (isExclusive){
            val navOptionBuilder = NavOptions.Builder()
            navOptionBuilder.setLaunchSingleTop(true)
            navOptionBuilder.setPopUpTo(R.id.info_navigation, true)
            return navOptionBuilder.build()
        }
        return null
    }


    /**
     * take a note:
     * - default is show INFO
     * - when show from default, we put all with exclusive flag
     */
    private fun defaultShowWhat(){
        when(mShowWhat){
            SHOW_TERM-> {showTerm(true)}
            SHOW_LICENSE-> {showLicense(true)}
            else->{/*do nothing, because default is show info*/}
        }
    }

}
