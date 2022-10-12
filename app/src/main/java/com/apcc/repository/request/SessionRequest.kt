package com.apcc.repository.request

import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *  request one of UserName or EmailAddress
 *  */
class SessionRequest : BaseRequest() {

    @SerializedName("UserName")
    @Expose
    var userName:String?=""
    @SerializedName("EmailAddress")
    @Expose
    var emailAddress:String?=""
    @SerializedName("PasswordContent")
    @Expose
    var passwordContent:String?=""

    fun getAccount():String?{
        return if (TextUtils.isEmpty(userName)) emailAddress else userName
    }
}