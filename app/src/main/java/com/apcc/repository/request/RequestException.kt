package com.apcc.repository.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.apcc.utils.RequestExceptionType

class RequestException : BaseRequest() {

    @SerializedName("Email")
    @Expose
    var email:String = ""
    @SerializedName("NewEmail")
    @Expose
    var newEmail:String = ""
    @SerializedName("Account")
    @Expose
    var account:String = ""
    @SerializedName("Code")
    @Expose
    var code:String = ""
    @SerializedName("OldPass")
    @Expose
    var oldPass:String = ""
    @SerializedName("PasswordContent")
    @Expose
    var passwordContent:String = ""
    @SerializedName("RequestType")
    @Expose
    var requestType:String = RequestExceptionType.REQUEST_RESET_PASSWORD
}