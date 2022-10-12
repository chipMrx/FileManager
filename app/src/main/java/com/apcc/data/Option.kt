package com.apcc.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import com.apcc.framework.AppManager
import com.apcc.utils.DataHelper
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["optionID"])
data class Option(
    @field:SerializedName("OptionID")
    @Expose
    var optionID:String = DataHelper.generateID(), // hide field
    @field:SerializedName("OptionIDParent")
    @Expose
    var optionIDParent:String = DataHelper.generateID(), // hide field
    @field:SerializedName("Title")
    @Expose
    var title:String = "",
    @field:SerializedName("Description")
    @Expose
    var description:String = "",

    @field:SerializedName("OptionType")  // hide field
    @Expose
    var optionType:String = "", // #{CustomOptionType}

    @field:SerializedName("Str1")
    @Expose
    var str1:String = "",
    @field:SerializedName("Str2")
    @Expose
    var str2:String = "",
    @field:SerializedName("Str3")
    @Expose
    var str3:String = "",
    @field:SerializedName("Str4")
    @Expose
    var str4:String = "",
    @field:SerializedName("Str5")
    @Expose
    var str5:String = "",

    @field:SerializedName("Int1")
    @Expose
    var int1:Int = 0,
    @field:SerializedName("Int2")
    @Expose
    var int2:Int = 0,
    @field:SerializedName("Int3")
    @Expose
    var int3:Int = 0,
    @field:SerializedName("Int4")
    @Expose
    var int4:Int = 0,
    @field:SerializedName("Int5")
    @Expose
    var int5:Int = 0,

    @field:SerializedName("UserID")
    @Expose
    var userID:String = AppManager.userID?:""  // hide field
) : Parcelable {


    //constructor()

    constructor(parcel: Parcel) : this() {
        optionID = parcel.readString()?:""
        optionIDParent = parcel.readString()?:""
        title = parcel.readString()?:""
        description = parcel.readString()?:""
        optionType = parcel.readString()?:""
        str1 = parcel.readString()?:""
        str2 = parcel.readString()?:""
        str3 = parcel.readString()?:""
        str4 = parcel.readString()?:""
        str5 = parcel.readString()?:""
        int1 = parcel.readInt()
        int2 = parcel.readInt()
        int3 = parcel.readInt()
        int4 = parcel.readInt()
        int5 = parcel.readInt()
        userID = parcel.readString()?:""
    }


    constructor(optionID:String, title: String):this(){
        this.optionID = optionID
        this.title = title
    }

    override fun toString(): String {
        return "$optionType : $title"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(optionID)
        parcel.writeString(optionIDParent)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(optionType)
        parcel.writeString(str1)
        parcel.writeString(str2)
        parcel.writeString(str3)
        parcel.writeString(str4)
        parcel.writeString(str5)
        parcel.writeInt(int1)
        parcel.writeInt(int2)
        parcel.writeInt(int3)
        parcel.writeInt(int4)
        parcel.writeInt(int5)
        parcel.writeString(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Option> {
        override fun createFromParcel(parcel: Parcel): Option {
            return Option(parcel)
        }

        override fun newArray(size: Int): Array<Option?> {
            return arrayOfNulls(size)
        }
    }
    
}