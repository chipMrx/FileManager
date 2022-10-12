package com.apcc.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.apcc.framework.AppManager
import com.apcc.utils.DataHelper
import com.apcc.utils.DateHelper
import com.google.gson.annotations.SerializedName
import com.apcc.utils.SyncType
import com.google.gson.annotations.Expose

@Entity
data class DelaySyncData(
    @field:SerializedName("SyncID")
    @Expose
    @PrimaryKey var syncID:String ="",
    @field:SerializedName("DataID")
    @Expose
    var dataID:String ="",
    @field:SerializedName("TableName")
    @Expose
    var tableName:String ="",
    @field:SerializedName("RequestType")
    @Expose
    var requestType:String = SyncType.SAVE,
    @field:SerializedName("CreatedDate")
    @Expose
    var createdDate:String ="",
    @field:SerializedName("UserID")
    @Expose
    var userID:String = AppManager.userID?:""  // hide field
) {

    constructor(dataID:String, tableName: String, requestType:String) : this() {
        this.syncID = DataHelper.generateID()
        this.dataID = dataID
        this.tableName = tableName
        this.requestType = requestType
        this.createdDate = DateHelper.now()
        this.userID = AppManager.userID?:""
    }

    @Ignore
    var dataObj:Any? = null
}