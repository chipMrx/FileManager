package com.apcc.base.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.apcc.base.fragment.AbsFragment
import com.apcc.emma.R
import com.apcc.emma.ui.file.FileActivity
import com.apcc.framework.AppManager
import com.apcc.utils.ExceptionHandler
import com.apcc.utils.Util
import com.apcc.view.XProgress
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject



abstract class BaseActivity: AppCompatActivity(), HasSupportFragmentInjector {
    //////////////////////////////////
    //// entity
    ////////////////////////////////////
    protected abstract val resourceLayoutId: Int
    protected abstract fun onInit()

    private var root: View? = null
    var mNavController: NavController?=null
    var xProgress:XProgress?=null
    private var isProgressShow = false
    private val activityLauncher = BaseActivityResult.registerActivityForResult(this)

    //////////////////////////////////
    //// sys
    ////////////////////////////////////
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Util.makeLoveWithGirlFriend(AppManager.instance.javaClass, AppManager.instance,  "vm", false)
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        val viewDataBinding: ViewDataBinding? = DataBindingUtil.setContentView(this, resourceLayoutId)
        if (viewDataBinding != null) {
            root = viewDataBinding.root
        }
        xProgress = findViewById(R.id.xProgress)
        onInit()
    }

    override fun onStart() {
        super.onStart()
        val isRunning = AppManager.instance.flagValid
        if (isRunning == null || !isRunning) { // app has been killed, maybe data not right
            reopenApp()
        }else
            showProgress(isProgressShow)
    }

    override fun onStop() {
        cancelDelayHideProgress()
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return onHomeClick()
            }
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return allowKeyBack() && super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    //////////////////////////////////
    //// support
    ////////////////////////////////////

    private fun getCurrentFragment(fragmentManager: FragmentManager):Fragment?{
        if (fragmentManager.fragments.isNotEmpty()){// exist item at position 0
            if (fragmentManager.fragments[0] is NavHostFragment ){
                return getCurrentFragment(fragmentManager.fragments[0].childFragmentManager)
            } else if (fragmentManager.fragments[0] is Fragment){ // every things is fragment
                return fragmentManager.fragments[0]
            }
        }
        return null
    }

    open fun getCurrentFragment():Fragment?{
        return getCurrentFragment(supportFragmentManager)
    }

    open fun allowKeyBack():Boolean{
        return askFragmentBack()
    }

    open fun onHomeClick():Boolean{
        // waiting progress
        xProgress?.let {
            if (it.visibility == View.VISIBLE)
                return false
        }
        if (!askFragmentBack()){ // fragment not allow
            return false
        }
        onBackPressed()
        return true
    }

    /**
     * when override, keep parent content
     */
    open fun showProgress(isShow:Boolean){
        isProgressShow = isShow
        if (isProgressShow){
            cancelDelayHideProgress()
            runOnUiThread {
                xProgress?.let {
                    it.visibility = if (isProgressShow) View.VISIBLE else View.GONE
                }
            }
        }else{
            delayHideProgress()
        }
    }

    private val mHandlerProgress = Handler(Looper.getMainLooper())
    private val mRunnableProgress = Runnable {
        runOnUiThread {
            xProgress?.let {
                it.visibility = if (isProgressShow) View.VISIBLE else View.GONE
            }
        }
    }

    private fun delayHideProgress(){
        cancelDelayHideProgress()
        mHandlerProgress.postDelayed(mRunnableProgress, 200)
    }

    private fun cancelDelayHideProgress(){
        mHandlerProgress.removeCallbacks(mRunnableProgress)
    }

    private fun askFragmentBack():Boolean{
        // waiting progress
        xProgress?.let {
            if (it.visibility == View.VISIBLE)
                return false
        }
        val fragment = getCurrentFragment()
        if (fragment is AbsFragment){
            return fragment.allowToBack()
        }
        return true //fragment default is allow back
    }


    open fun getRoot(): View? {
        return root
    }

    open fun reopenApp() {
        val intent = Intent(this, FileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun popBackStack(className: String) {
        mNavController?.currentDestination?.let {
            if( (it as FragmentNavigator.Destination).className == className )
                mNavController?.popBackStack()
        }
        if (mNavController?.currentDestination ==null){
            finish() //  if end of stack
        }
    }

    fun lunchForResult(intent: Intent, onActivityResult: BaseActivityResult.OnActivityResult? = null){
        activityLauncher.launch(intent, onActivityResult)
    }
}