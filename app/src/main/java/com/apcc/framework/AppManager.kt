package com.apcc.framework

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.apcc.emma.BuildConfig
import com.apcc.di.AppInjector
import com.apcc.emma.ui.file.FileActivity
import com.apcc.service.SyncDataService
import com.apcc.utils.Util
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import javax.inject.Inject


class AppManager: Application(), HasActivityInjector, HasServiceInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    companion object{
        lateinit var instance: AppManager
        var userID: String?=null
        var sessionID: String?=null
        val connection = MutableLiveData(false)
        var mFlag = "YnJlYWt0aHJvdWdo"

        var qlwej = HashMap<String, Boolean>()
        private var syncFlag = false

        fun isLogged():Boolean{
            return !TextUtils.isEmpty(userID)
        }

        fun initSession(user: String? = "", session: String? = ""){
            this.userID = user
            this.sessionID = session
        }

        fun cleanSession(){
            initSession()
            CacheManager.cleanSessionInfo(instance)
            Util.cleanAppTask(instance)
        }

        fun newTaskStack(context: Context? = instance){
            context?.let { ctx->
                val intent = Intent(ctx, FileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            }
        }

        fun updateSync(flag:Boolean, noteToSave:Boolean = false){
            if (!TextUtils.isEmpty(userID)){
                syncFlag = flag
                if (::instance.isInitialized){
                    if (noteToSave){
                        CacheManager.saveSyncFlag(instance, flag)
                    }
                    instance.startService()
                }
            }else{ // not exist user to sync
                syncFlag = false
            }
        }
        fun aczzu(str:String, cuza:Boolean):Boolean{
            // check allow function for re-call
            if (qlwej.containsKey(str)){
                if (cuza != qlwej[str]){
                    qlwej[str] = cuza
                    return true
                }else{
                    return qlwej[str]?:false
                }
            }else{
                qlwej[str] = cuza
                return true
            }
        }

    }

    var flagValid: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        System.loadLibrary("emma")
        AppInjector.init(this)
        //// init user info
        userID = CacheManager.getUserID(this)
        sessionID = CacheManager.getSession(this)
        startInternetListener()
        listenerSyncFlag()
    }

    external fun vm()

    override fun activityInjector() = activityInjector
    override fun serviceInjector() = serviceInjector

    fun setFlag(flag:String){
        mFlag = flag
    }

    fun getFlag() = mFlag

    fun makeLove() = BuildConfig.DEBUG
    fun groupFuckLog() = BuildConfig.VERSION_CODE
    fun totalBitchLog() = BuildConfig.BUILD_TIME
    fun asshole() = BuildConfig.VERSION_NAME

    private fun startInternetListener(){
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        if ( connectivityManager is ConnectivityManager){
            val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    connection.postValue(true)
                    startService(true)
                }

                override fun onLost(network: Network) {
                    connection.postValue(false)
                    stopService()
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
                connectivityManager.registerNetworkCallback(request, networkCallback)
            }
        }
    }

    private fun listenerSyncFlag(){
        CacheManager.listenerCacheChange(this) { sharedPreferences, key ->
            when (key) {
                CacheManager.KEY_REQUEST_SYNC -> {
                    updateSync(sharedPreferences.getBoolean(key, false))
                }
            }
        }
    }

    private fun startService(connected:Boolean? = connection.value) {
        if (syncFlag && connected == true){
            if (Util.checkServiceRunning(this, SyncDataService::class.java.name)){
                return
            }
            // check service is working?
            // start service
            val serviceIntent = Intent(this, SyncDataService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    private fun stopService() {
        if (Util.checkServiceRunning(this, SyncDataService::class.java.name)){
            val serviceIntent = Intent(this, SyncDataService::class.java)
            stopService(serviceIntent)
        }
    }
}