package com.apcc.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiTransfer (
    @SerializedName("BodyData")
    @Expose
    var bodyData:String? = ""
)