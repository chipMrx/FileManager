package com.apcc.repository.request

import android.text.TextUtils
import com.apcc.emma.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.apcc.framework.AppManager
import com.apcc.utils.DateHelper
import com.apcc.utils.SessionType
import com.apcc.utils.Util

open class BaseRequest {
    @SerializedName("SessionID")
    @Expose
    var sessionID:String=AppManager.sessionID?:""

    @SerializedName("SessionType")
    @Expose
    var sessionType:String?= SessionType.APP_ANDROID
    @SerializedName("BuildType")
    @Expose
    var buildType:String?= BuildConfig.BUILD_TYPE

    @SerializedName("UserID")
    @Expose
    var userID:String=AppManager.userID?:""

    @SerializedName("DeviceID")
    @Expose
    var deviceID:String= Util.getDeviceID(AppManager.instance)

    @SerializedName("IDs")
    @Expose
    var ids:List<String>? = ArrayList()



    @SerializedName("id")
    @Expose
    var id:String? = null

    /**
     * take a note
     * we sync one by one
     */
    @SerializedName("SyncID")
    @Expose
    var syncID:String? = null

    @SerializedName("Time")
    @Expose
    var time:String = DateHelper.now()

    /////////////////////////
    @SerializedName("Limit")
    var limit:Int = 0
    @SerializedName("Offset")
    var offset:Int = 0

    fun isSyncRequest() = !TextUtils.isEmpty(syncID)
}