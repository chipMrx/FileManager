package com.apcc.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.apcc.framework.AppManager
import com.apcc.utils.DataHelper
import com.apcc.utils.DateHelper
import com.apcc.utils.FileHelper
import com.google.gson.annotations.SerializedName
import com.apcc.utils.FileType
import com.google.gson.annotations.Expose

@Entity
data class FileApp(
    @field:SerializedName("FileAppID")
    @Expose
    @PrimaryKey var fileAppID:String="",
    @field:SerializedName("Path")
    @Expose
    var path:String?="",
    @field:SerializedName("FileExtension")
    @Expose
    var fileExtension:String?="",
    @field:SerializedName("FileType")
    @Expose
    var fileType:Int= FileType.UNKNOW,
    @field:SerializedName("FileDescription")
    @Expose
    var fileDescription:String?="",
    @field:SerializedName("FileSize")
    @Expose
    var fileSize:Double = 0.0,
    @field:SerializedName("CreatedBy")
    @Expose
    var createdBy:String?="",
    @field:SerializedName("CreatedDate")
    @Expose
    var createdDate:String? = "",
    @field:SerializedName("ModifiedBy")
    @Expose
    var modifiedBy:String?="",
    @field:SerializedName("ModifiedDate")
    @Expose
    var modifiedDate:String? = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor(fileAppID:String) : this(){
        this.fileAppID = fileAppID

        createdBy = AppManager.userID
        createdDate = DateHelper.now()

        modifiedBy = createdBy
        modifiedDate = createdDate
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileAppID)
        parcel.writeString(path)
        parcel.writeString(fileExtension)
        parcel.writeInt(fileType)
        parcel.writeString(fileDescription)
        parcel.writeDouble(fileSize)
        parcel.writeString(createdBy)
        parcel.writeString(createdDate)
        parcel.writeString(modifiedBy)
        parcel.writeString(modifiedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileApp> {
        override fun createFromParcel(parcel: Parcel): FileApp {
            return FileApp(parcel)
        }

        override fun newArray(size: Int): Array<FileApp?> {
            return arrayOfNulls(size)
        }
    }


    fun getTitle():String{
        return FileHelper.getFileName(path?:"")
    }

    fun getDate(format:String = DateHelper.FORMAT_DATE_TIME_SHORT):String{
        return DateHelper.reformatDateTime(inputDateString = modifiedDate, outputDateFormat = format)?:""
    }

    fun getFileSizeDisplay():String{
        return DataHelper.readableFileSize(fileSize) // FileHelper.getFileSizeDisplace(fileSize)
    }

    override fun toString(): String {
        return fileAppID
    }

    @Ignore
    fun getMap():Map<String, String?>{
        val map = HashMap<String, String?>()

        map["FileAppID"] = fileAppID
        map["Path"] = path
        map["FileExtension"] = fileExtension
        map["FileType"] = fileType.toString()
        map["FileDescription"] = fileDescription
        map["FileSize"] = fileSize.toString()
        map["CreatedBy"] = createdBy
        map["CreatedDate"] = createdDate
        map["ModifiedBy"] = modifiedBy
        map["ModifiedDate"] = modifiedDate

        return map
    }

    override fun hashCode(): Int {
        //return super.hashCode()
        return fileAppID.hashCode()
    }
}