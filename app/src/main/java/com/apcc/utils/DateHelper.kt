package com.apcc.utils

import android.text.TextUtils
import android.widget.DatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    const val FORMAT_TIME = "HH:mm:ss"
    const val FORMAT_TIME_SHORT = "HH:mm"
    const val FORMAT_DATE = "dd/MM/yyyy"
    const val FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm:ss"
    const val FORMAT_DATE_TIME_SHORT = "dd/MM/yyyy HH:mm"

    const val FORMAT_DATABASE_TIME = "yyyy/MM/dd HH:mm:ss.SSS"

    ////////////////////////////////////////////
    // time as long >< string
    ////////////////////////////////////////////
    /**
     *
     * @param format : any type of date , time format
     * @param data :date, date time or time
     * @return : type of millis seconds
     */
    fun stringToDateTime(data: String, format: String = FORMAT_DATABASE_TIME): Long {
        if (data.isEmpty() || format.isEmpty() || data.length < format.length)
            return 0
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        try {
            return formatter.parse(data)?.time?:0
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun dateTimeToString(data:Long,format: String = FORMAT_DATABASE_TIME):String{
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = data
        return formatter.format(calendar.time)
    }

    ////////////////////////////////////////////
    // time as string >< string
    ////////////////////////////////////////////

    fun now():String{
        val formatter = SimpleDateFormat(FORMAT_DATABASE_TIME, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return formatter.format(calendar.time)
    }

    fun formatToShow(dateTime:String?, showFormat:String? = FORMAT_DATE_TIME_SHORT):String{
        return reformatDateTime(dateTime, FORMAT_DATABASE_TIME, showFormat)
    }

    fun reformatDateTime(inputDateString:String?, inputDateFormat:String? = FORMAT_DATABASE_TIME , outputDateFormat:String?):String{
        if (TextUtils.isEmpty(inputDateString)
            || TextUtils.isEmpty(inputDateFormat)
            || TextUtils.isEmpty(outputDateFormat))
            return ""

        val formatterInput = SimpleDateFormat(inputDateFormat!!, Locale.getDefault())
        val formatterOutput = SimpleDateFormat(outputDateFormat!!, Locale.getDefault())

        var outputDateString = ""
        try {
            val date = formatterInput.parse(inputDateString!!)
            outputDateString = formatterOutput.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateString
    }

    ////////////////////////////////////////////
    // support
    ////////////////////////////////////////////
    fun setCurrentDate(datePicker: DatePicker, currentDate:Long = System.currentTimeMillis()){
        val calendarTemp = Calendar.getInstance()
        calendarTemp.timeInMillis = currentDate
        val year = calendarTemp.get(Calendar.YEAR)
        val month = calendarTemp.get(Calendar.MONTH)
        val day = calendarTemp.get(Calendar.DAY_OF_MONTH)
        datePicker.updateDate(year, month, day)
    }
}