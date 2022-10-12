package com.apcc.base.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType
import javax.inject.Inject


abstract class BaseActivityVM<VM : ViewModel>: BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(getVMClass())
    }

    /////////////////////
    // support fun
    /////////////////////////
    //Get the actual type of generic T
    @Suppress("UNCHECKED_CAST")
    private fun getVMClass(): Class<VM> {// 0: data binding ; 1: view model
        return ((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>)
    }
}