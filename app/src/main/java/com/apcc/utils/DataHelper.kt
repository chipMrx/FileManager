package com.apcc.utils

import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Patterns
import android.webkit.URLUtil
import com.apcc.data.FileApp
import com.apcc.data.Option
import com.apcc.framework.AppManager
import com.apcc.framework.CacheManager
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow


object DataHelper {
    const val FORMAT_NUMBER = "#,###,###.##"
    const val DECIMAL_SEPARATOR = "."
    const val GROUP_SEPARATOR = ","

    fun generateID(): String {
        val id = UUID.randomUUID()
        return id.toString()
    }

    fun numberAsText(number: Number?):String{
        number?.let {
            val df = DecimalFormat(FORMAT_NUMBER)
            return df.format(it)
        }
        return ""
    }


    /**
     * clear all character isn't number
     */
    fun cleanNumber(s: String):String {
        return if (TextUtils.isEmpty(s)) {
            ""
        } else {
            try {
                // clean wrong number format
                s.replace("[^0-9.]".toRegex(), "")
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun cleanNumberInt(s: String):String {
        return if (TextUtils.isEmpty(s)) {
            ""
        } else {
            try {
                // get integer only
                if ( s.contains(DECIMAL_SEPARATOR)) {
                    s.substring(0, s.indexOf(DECIMAL_SEPARATOR))
                }else{
                    // clean wrong number format
                    s.replace("[^0-9.]".toRegex(), "")
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun textToDouble(s: String?):Double{
        val textVal = cleanNumber(s ?: "")
        if (!TextUtils.isEmpty(textVal)){
            return textVal.toDouble()
        }
        return 0.0
    }

    fun textToInt(s: String):Int{
        val  textVal = cleanNumberInt(s)
        if (!TextUtils.isEmpty(textVal)){
            return textVal.toInt()
        }
        return 0
    }

    fun textToHtml(text: String):Spanned?{
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
                text,
                Html.FROM_HTML_MODE_LEGACY
            )
            else -> Html.fromHtml(text)
        }
    }

    fun fileAppToOptionList(fileApps: MutableList<FileApp>):ArrayList<Option>{
        val options = ArrayList<Option>()
        for (fileApp in fileApps){
            fileApp.path?.let {
                options.add(Option())
            }
        }
        return options
    }

    fun deAccent(str: String?): String? {
        // missing đ
//        val nfdNormalizedString: String = Normalizer.normalize(str, Normalizer.Form.NFD)
//        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
//        return pattern.matcher(nfdNormalizedString).replaceAll("")

        return str?.replace("[àáạảãâầấậẩẫăằắặẳẵ]".toRegex(), "a")
           ?.replace("[èéẹẻẽêềếệểễ]".toRegex(), "e")
           ?.replace("[ìíịỉĩ]".toRegex(), "i")
           ?.replace("[òóọỏõôồốộỗơờợởỡ]".toRegex(), "o")
           ?.replace("[ùúụủũưừứựửữ]".toRegex(), "u")
           ?.replace("[ỳýỵỷỹ]".toRegex(), "y")
           ?.replace("[đ]".toRegex(), "d")
           ?.replace("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]".toRegex(), "A")
           ?.replace("[ÈÉẸẺẼÊỀẾỆỂỄ]".toRegex(), "E")
           ?.replace("[ÌÍỊỈĨ]".toRegex(), "I")
           ?.replace("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]".toRegex(), "O")
           ?.replace("[ÙÚỤỦŨƯỪỨỰỬỮ]".toRegex(), "U")
           ?.replace("[ỲÝỴỶỸ]".toRegex(), "Y")
           ?.replace("[Đ]".toRegex(), "D")
    }

    //////////////////////////////////
    // support
    /////////////////////////////////
    fun readableFileSize(size: Double): String {
        if (size <= 0) {
            return "0"
        }

        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }

    /**
     * @param ignoreCase: compare ignore uppercase / lowercase
     */
    fun compareString(str1: CharSequence?, str2: CharSequence?, ignoreCase: Boolean = false):Int{
        if (str1 != null){
            if (str2 != null)// both of theme not null
                return str1.toString().compareTo(str2.toString(), ignoreCase)
            else // str2 is null
                return 1
        }else{
            if (str2 != null) // str1 is null
                return -1
            else // both of theme is null
                return 0
        }
    }

    fun supportContains(longVal: Long, strSub: CharSequence?):Boolean{
        return supportContains(longVal.toString(), strSub)
    }

    fun supportContains(str: CharSequence?, strSub: CharSequence?):Boolean{
        if (str == null || strSub == null)
            return false
        val strTemp = deAccent(str.toString())?:""
        val strSubTemp = deAccent(strSub.toString())?:""
        return strTemp.contains(strSubTemp, true)
    }

    external fun getServerApi():String

    external fun getSubHost():String

    fun formatFileUrl(fileName: String?):String{
        if (fileName == null || fileName.isEmpty())
            return ""
        if (isValidUrl(fileName)
            || isValidLocalFileURL(fileName)){
            return fileName
        } // else => format as online file
        val host = getServerApi()
        val sub = getSubHost()
        val subFile = CacheManager.getUrlPathFile(AppManager.instance)

        var url = mergeUrl(host, sub)
        url = mergeUrl(url, subFile)
        url = mergeUrl(url, fileName)

        return url
    }

    fun mergeUrl(line1: String, line2: String): String {
        var tempLine1 = line1
        var tempLine2 = line2
        ////
        if (tempLine1.endsWith('/') ){
            tempLine1 = tempLine1.substring(0, tempLine1.length - 1)
        }
        if (tempLine2.startsWith('/') ){
            tempLine2 = tempLine2.substring(1)
        }
        ///
        return "$tempLine1/$tempLine2"
    }

    //////////////////////////////////
    // valid input
    /////////////////////////////////
    fun isValidIDLength(id: String?):Boolean{
        return id != null && id.isNotEmpty() && id.length <= FieldLength.ID
    }

    fun isValidMediumLength(input: String?, requireLength: Int = 0):Boolean{
        return input != null && input.length <= FieldLength.MEDIUM && input.length >= requireLength
    }

    fun isValidLargeLength(input: String?, requireLength: Int = 0):Boolean{
        return input != null && input.length <= FieldLength.LARGE && input.length >= requireLength
    }

    fun isValidHugeLength(input: String?, requireLength: Int = 0):Boolean{
        return input != null && input.length <= FieldLength.HUGE && input.length >= requireLength
    }

    /**
     * 6-100
     */
    fun isValidPass(pass: String?):Boolean{
        return isValidMediumLength(pass, 6)
    }

    /**
     * 6-200
     */
    fun isValidAccount(account: String?):Boolean{
        return isValidLargeLength(account, 6)
    }

    /**
     * 6-200
     */
    fun isValidMail(mail: String?):Boolean{
        return isValidLargeLength(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    /**
     * 5
     */
    fun isValidCode(code: String?):Boolean{
        return code != null && code.length == 5
    }

    fun isValidUrl(url: String?):Boolean{
        return URLUtil.isValidUrl(url)
    }

    fun isValidLocalFileURL(fileUrl: String?):Boolean{
//        return true
        return FileHelper.checkExistFile(fileUrl)
    }

    /**
     *  VIETTEL
     *  086. 096. 097. 098. 032. 033. 034. 035. 036. 037. 038. 039.
     *  ___________________________________________________________________
     *  MOBI FONE
     *  089. 090. 093. 070. 079. 077. 076. 078.
     *  ___________________________________________________________________
     *  VINA PHONE
     *  091. 094. 083. 084. 085. 081. 082.
     *  ___________________________________________________________________
     *  VIETNAM MOBILE
     *  092. 056. 058
     */
    fun getSimCardProviderName(phoneNumber:String):String{
        PhoneNumberUtils.extractNetworkPortion("0906018945")
        return ""
    }

    fun contains(arrInt:IntArray, intVal:Int ):Boolean{
        for (temp in arrInt){
            if (temp == intVal)
                return true
        }
        return false
    }

    fun getMin(vararg intVals:Int):Int{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var min = intVals[0]
        for (dataVal in intVals){
            if (dataVal < min)
                min = dataVal
        }
        return min
    }

    fun getMax(vararg intVals:Int):Int{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var max = intVals[0]
        for (dataVal in intVals){
            if (dataVal > max)
                max = dataVal
        }
        return max
    }

    fun getMin(vararg intVals:Float):Float{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var min = intVals[0]
        for (dataVal in intVals){
            if (dataVal < min)
                min = dataVal
        }
        return min
    }

    fun getMax(vararg intVals:Float):Float{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var max = intVals[0]
        for (dataVal in intVals){
            if (dataVal > max)
                max = dataVal
        }
        return max
    }

    fun getMin(vararg intVals:Double):Double{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var min = intVals[0]
        for (dataVal in intVals){
            if (dataVal < min)
                min = dataVal
        }
        return min
    }

    fun getMax(vararg intVals:Double):Double{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var max = intVals[0]
        for (dataVal in intVals){
            if (dataVal > max)
                max = dataVal
        }
        return max
    }

    fun getAverage(vararg intVals:Int):Int{
        if (intVals.isEmpty()){
            throw Exception("No int val!")
        }
        var total = 0
        for (dataVal in intVals){
            total += dataVal
        }
        return total/intVals.size
    }
}