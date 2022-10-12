package com.apcc.base.dialog

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apcc.di.Injectable
import java.lang.reflect.ParameterizedType
import javax.inject.Inject

abstract class BaseDialogVM<BD : ViewDataBinding, VM : ViewModel> : BaseDialog<BD>(), Injectable {
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
        return ((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>)
    }
}