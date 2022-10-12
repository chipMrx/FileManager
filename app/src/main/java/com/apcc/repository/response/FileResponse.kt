package com.apcc.repository.response

import com.apcc.data.FileApp
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FileResponse:BaseResponse() {

    @SerializedName("FileApp")
    @Expose
    var file: FileApp? = null
    @SerializedName("ListFileApp")
    @Expose
    var listFile: List<FileApp>? = null
}
