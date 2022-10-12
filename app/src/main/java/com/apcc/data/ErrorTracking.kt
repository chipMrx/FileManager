package com.apcc.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.apcc.framework.AppManager
import com.apcc.utils.DataHelper
import com.apcc.utils.DateHelper
import com.google.gson.annotations.Expose

@Entity
data class ErrorTracking(
    @field:SerializedName("ErrorID")
    @Expose
    @PrimaryKey var errorID:String = DataHelper.generateID(),
    @field:SerializedName("SDKVersion")
    @Expose
    var sdkVersion:String? = "",
    @field:SerializedName("ReleaseVersion")
    @Expose
    var releaseVersion:Int = 0,
    @field:SerializedName("ReleaseName")
    @Expose
    var releaseName:String? = "",
    @field:SerializedName("ReleaseTime")
    @Expose
    var releaseTime:String? = "",
    @field:SerializedName("Incremental")
    @Expose
    var incremental:String? = "",
    @field:SerializedName("BuildBrand")
    @Expose
    var buildBrand:String? = "",
    @field:SerializedName("BuildDevice")
    @Expose
    var buildDevice:String? = "",
    @field:SerializedName("BuildID")
    @Expose
    var buildID:String? = "",
    @field:SerializedName("Model")
    @Expose
    var model:String? = "",
    @field:SerializedName("Product")
    @Expose
    var product:String? = "",
    @field:SerializedName("StackTrace")
    @Expose
    var stackTrace:String? = "",
    @field:SerializedName("ErrorComment")
    @Expose
    var errorComment:String? = "",
    @field:SerializedName("CreatedBy")
    @Expose
    var createdBy:String? = AppManager.userID,
    @field:SerializedName("CreatedDate")
    @Expose
    var createdDate:String? = DateHelper.now()) : Parcelable {

    constructor(parcel: Parcel) : this() {
        sdkVersion = parcel.readString()?:""
        releaseVersion = parcel.readInt()
        releaseName = parcel.readString()?:""
        releaseTime = parcel.readString()
        incremental = parcel.readString()?:""
        buildBrand = parcel.readString()?:""
        buildDevice = parcel.readString()?:""
        buildID = parcel.readString()?:""
        model = parcel.readString()?:""
        product = parcel.readString()?:""
        stackTrace = parcel.readString()?:""
        errorComment = parcel.readString()?:""
        createdBy = parcel.readString()?:""
        createdDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sdkVersion)
        parcel.writeInt(releaseVersion)
        parcel.writeString(releaseName)
        parcel.writeString(releaseTime)
        parcel.writeString(incremental)
        parcel.writeString(buildBrand)
        parcel.writeString(buildDevice)
        parcel.writeString(buildID)
        parcel.writeString(model)
        parcel.writeString(product)
        parcel.writeString(stackTrace)
        parcel.writeString(errorComment)
        parcel.writeString(createdBy)
        parcel.writeString(createdDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ErrorTracking> {
        override fun createFromParcel(parcel: Parcel): ErrorTracking {
            return ErrorTracking(parcel)
        }

        override fun newArray(size: Int): Array<ErrorTracking?> {
            return arrayOfNulls(size)
        }
    }
}