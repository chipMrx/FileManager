package com.apcc.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apcc.binding.FragmentDataBindingComponent
import com.apcc.di.Injectable
import com.apcc.utils.AutoClearedValue
import com.apcc.utils.ExceptionHandler
import com.apcc.utils.Util
import com.apcc.base.activity.BaseActionBarActivity
import com.apcc.base.activity.BaseActivity
import java.lang.reflect.ParameterizedType
import javax.inject.Inject

abstract class BaseFragment<BD : ViewDataBinding, VM : ViewModel> : AbsFragment() ,Injectable{
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    protected var binding by AutoClearedValue<BD>()
    protected  val dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    protected val viewModel : VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(getVMClass())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(requireActivity()))
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, resourceLayoutId, container, false, dataBindingComponent)
        binding.lifecycleOwner = viewLifecycleOwner
        onInitView(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUi()
    }

    override fun onResume() {
        super.onResume()
        showScreenName(screenName)
        showHomeUpAsButton()
    }

    protected fun showScreenName(screenName:String?) {
        this.screenName = screenName
        (activity as? BaseActionBarActivity)?.updateTitle(screenName)
    }

    protected fun showHomeUpAsButton() {
        (activity as? BaseActionBarActivity)?.enableHomeAsBackButton(enableHomeAsBackButton())
    }



    override fun onStop() {
        super.onStop()
        Util.hideSoftKeyboard(binding.root)
    }


    open fun stackIndex():Boolean{
        return false
    }

    /**
     * when override, keep parent content
     */
    open fun showProgress(isShow:Boolean){
        if (activity != null && activity is BaseActivity)
            (activity as BaseActivity).showProgress(isShow)
    }

    /////////////////////
    // support fun
    /////////////////////////
    //Get the actual type of generic T
    @Suppress("UNCHECKED_CAST")
    private fun getVMClass(): Class<VM> {// 0: data binding ; 1: view model
        return ((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>)
    }
}