package com.apcc.repository.request

import com.apcc.data.ErrorTracking
import com.apcc.data.Option
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.intellij.lang.annotations.Language

class CommonRequest : BaseRequest() {

    @SerializedName("Error")
    @Expose
    var error: ErrorTracking?=null

    @SerializedName("Option")
    @Expose
    var option: Option?=null

    @SerializedName("OptionList")
    @Expose
    var optionList: List<Option>?=null

    @SerializedName("Language")
    @Expose
    var language: Language?=null

}