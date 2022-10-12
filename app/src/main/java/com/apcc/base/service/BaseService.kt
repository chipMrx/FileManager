package com.apcc.base.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apcc.utils.Logger
import dagger.android.AndroidInjection
import java.lang.reflect.ParameterizedType
import javax.inject.Inject


abstract class BaseService<VM : ViewModel> : LifecycleService() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel : VM

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        Logger.d("init viewModel ${::viewModelFactory.isInitialized }")
        viewModel = viewModelFactory.create(getVMClass())
    }


    /////////////////////
    // support fun
    /////////////////////////

    @SuppressLint("WrongConstant")
    protected fun createNotificationChannel(channelID:String, channelName:String):String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            return channelID
        }else
            return ""
    }

    protected fun dismissNotifyCallComing(notificationID:Int) {
        getSystemService(NotificationManager::class.java).cancel(notificationID)
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