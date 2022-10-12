package com.apcc.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.apcc.utils.DataHelper
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class Language(
    @PrimaryKey
    @field:SerializedName("LanguageID")
    @Expose
    var languageID:String="",
    @field:SerializedName("LatinName")
    @Expose
    var latinName:String?="",
    @field:SerializedName("NativeName")
    @Expose
    var nativeName:String?="",
    @field:SerializedName("CodeType1")
    @Expose
    var codeType1:String?="",
    @field:SerializedName("CodeType2t")
    @Expose
    var codeType2t:String?="",
    @field:SerializedName("CodeType2b")
    @Expose
    var codeType2b:String?="",
    @field:SerializedName("CodeType3")
    @Expose
    var codeType3:String?="",
    @field:SerializedName("IsPicked")
    var isPicked:Boolean = false, // exclude from gson
    @field:SerializedName("Downloaded")
    var downloaded:Boolean = false, // exclude from gson
    @field:SerializedName("TrainedDataLink")
    @Expose
    var trainedDataLink:String?="",
    @field:SerializedName("LanguageFamily")
    @Expose
    var languageFamily:String? = "",
    @field:SerializedName("Notes")
    @Expose
    var notes:String? = ""
) : Parcelable {

    constructor(familyName:String, latinName:String, nativeName:String,
    codeType1: String, codeType2t: String,
    codeType2b: String, codeType3: String,
    notes: String, trainedDataLink:String):this(DataHelper.generateID(),
    latinName, nativeName, codeType1, codeType2t, codeType2b, codeType3,
        false, false, trainedDataLink, familyName, notes){

    }

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    fun getTitle():String{
        return nativeName?:""
    }

    fun getDescription():String{
        return "$latinName : $notes "
    }


    override fun toString(): String {
        return languageID
    }

    @Ignore
    fun getMap():Map<String, String?>{
        val map = HashMap<String, String?>()
        map["languageID"] = languageID
        map["latinName"] = latinName
        map["nativeName"] = nativeName
        map["codeType1"] = codeType1
        map["codeType2t"] = codeType2t
        map["codeType2b"] = codeType2b
        map["codeType3"] = codeType3
        map["isPicked"] = isPicked.toString()
        map["downloaded"] = downloaded.toString()
        map["trainedDataLink"] = trainedDataLink
        map["languageFamily"] = languageFamily
        map["notes"] = notes

        return map
    }

    override fun hashCode(): Int {
        //return super.hashCode()
        return languageID.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(languageID)
        parcel.writeString(latinName)
        parcel.writeString(nativeName)
        parcel.writeString(codeType1)
        parcel.writeString(codeType2t)
        parcel.writeString(codeType2b)
        parcel.writeString(codeType3)
        parcel.writeByte(if (isPicked) 1 else 0)
        parcel.writeByte(if (downloaded) 1 else 0)
        parcel.writeString(trainedDataLink)
        parcel.writeString(languageFamily)
        parcel.writeString(notes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Language> {
        override fun createFromParcel(parcel: Parcel): Language {
            return Language(parcel)
        }

        override fun newArray(size: Int): Array<Language?> {
            return arrayOfNulls(size)
        }
    }
}