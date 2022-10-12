package com.apcc.emma.ui.dialog.pickerdate

import android.os.Bundle
import android.view.View
import com.apcc.emma.R
import com.apcc.emma.databinding.DialogDatePickerBinding
import com.apcc.utils.DateHelper
import com.apcc.base.dialog.BaseDialog
import java.lang.Exception
import java.util.*

class DatePickerDialog:BaseDialog<DialogDatePickerBinding>(), Action {
    override val resourceLayoutId: Int
        get() = R.layout.dialog_date_picker

    override val TAG: String
        get() = "DatePickerDialog"
    companion object{

        private const val EXTRA_TITLE = "extraTitle"
        private const val EXTRA_START_DATE = "extraStartDate"
        private const val EXTRA_END_DATE = "extraEndDate"
        private const val EXTRA_CURRENT_DATE = "extraCurrentDate"

        /**
         * @param currentDate: using database format
         */
        fun newInstance(title:String? = null,
                        startDate:Any? = null,
                        endDate:Any? = null,
                        currentDate:Any? = null,
                        callback:Callback?=null): DatePickerDialog{
            var startDateLong:Long = 0
            if (startDate is String)
                startDateLong = DateHelper.stringToDateTime(startDate)
            else if (startDate is Long)
                startDateLong = startDate

            var endDateLong:Long = 0
            if (endDate is String)
                endDateLong = DateHelper.stringToDateTime(endDate)
            else if (endDate is Long)
                endDateLong = endDate

            var currentDateLong:Long = System.currentTimeMillis()
            if (currentDate is String)
                currentDateLong = DateHelper.stringToDateTime(currentDate)
            else if (currentDate is Long)
                currentDateLong = currentDate


            return newInstance(title,
                startDateLong,
                endDateLong,
                currentDateLong,
                callback)
        }

        fun newInstance(title:String? = null,
                        startDate:Long = 0,
                        endDate:Long = 0,
                        currentDate:Long = System.currentTimeMillis(),
                        callback:Callback?=null): DatePickerDialog{
            val dialog = DatePickerDialog()
            val bundle = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putLong(EXTRA_START_DATE, startDate)
                putLong(EXTRA_END_DATE, endDate)
                putLong(EXTRA_CURRENT_DATE, currentDate)
            }
            dialog.arguments = bundle
            dialog.mCallback = callback
            return dialog
        }
    }

    private var mTitle = ""
    private var mCallback:Callback?=null
    private var mStartDate:Long = 0
    private var mEndDate:Long = 0L
    private var mCurrentDate:Long = 0L

    override fun extraData() {
        arguments?.let {
            mTitle = it.getString(EXTRA_TITLE, getString(R.string.title_datePicker))
            mStartDate = it.getLong(EXTRA_START_DATE, 0)
            mEndDate = it.getLong(EXTRA_END_DATE, 0)
            mCurrentDate = it.getLong(EXTRA_CURRENT_DATE, System.currentTimeMillis())
        }
    }

    override fun onInitView(root: View?) {
    }

    override fun subscribeUi() {
        binding.action = this
        DateHelper.setCurrentDate(binding.dpkDate, mCurrentDate)
        if (mStartDate != 0L)
            binding.dpkDate.minDate = mStartDate
        if (mEndDate != 0L)
            binding.dpkDate.maxDate = mEndDate
    }

    override fun onCancelClick() {
        requestDismiss()
    }

    override fun onOkClick() {
        requestDismiss()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, binding.dpkDate.year)
        calendar.set(Calendar.MONTH, binding.dpkDate.month)
        calendar.set(Calendar.DAY_OF_MONTH, binding.dpkDate.dayOfMonth)

        mCallback?.onSelectedDate(calendar.timeInMillis)
    }

    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////
    private fun requestDismiss(){
        try {
            dismissAllowingStateLoss()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    ////////////////////////////////////////////////////////////
    ////
    //////////////////////////////////////////////////////
    interface Callback{
        fun onSelectedDate(millis:Long)
        fun onCancel()
    }


}