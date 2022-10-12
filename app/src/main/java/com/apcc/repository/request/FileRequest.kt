package com.apcc.repository.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.apcc.data.FileApp
import com.apcc.utils.FileType
import java.io.File

class FileRequest : BaseRequest() {

    @SerializedName("FileApp")
    @Expose
    var fileApp: FileApp?=null

    @SerializedName("File")
    @Expose
    var file: File?=null

    @SerializedName("FileType")
    @Expose
    var fileType: Int = FileType.UNKNOW

}