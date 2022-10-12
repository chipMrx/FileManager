package com.apcc.repository.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("SessionID")
    @Expose
    var sessionID: String? = ""

    @SerializedName("Message")
    @Expose
    var message: String? = ""

    @SerializedName("SessionState")
    @Expose
    var sessionState:Boolean? = false

    /////////////////////////
    @SerializedName("Limit")
    @Expose
    var limit:Int = 0
    @SerializedName("Offset")
    @Expose
    var offset:Int = 0
}