package com.apcc.repository.response

import com.apcc.data.ErrorTracking
import com.apcc.data.Language
import com.apcc.data.Option
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonResponse :BaseResponse() {

    @SerializedName("CgfUrlPathFile")
    @Expose
    val cgfUrlPathFile: String? = ""

    @SerializedName("CgfCardService")
    @Expose
    val cgfCardService: Boolean? = false

    @SerializedName("ErrorTracking")
    @Expose
    var error: ErrorTracking? = null

    @SerializedName("Option")
    @Expose
    var option: Option? = null

    @SerializedName("OptionList")
    @Expose
    var optionList: List<Option>? = null


    @SerializedName("Language")
    @Expose
    var language: Language? = null
}
