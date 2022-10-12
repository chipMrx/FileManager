package com.apcc.emma.ui.dialog.order

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.apcc.emma.R
import com.apcc.data.Option
import com.apcc.framework.AppManager
import com.apcc.repository.CommonRepository
import javax.inject.Inject

class OrderViewModel @Inject constructor(private val application: Application,
                                          private val commonRepository: CommonRepository) : ViewModel() {

    var mTitle:ObservableField<String> = ObservableField("")
    val errorMsg = ObservableField("")


    lateinit var mOptionType:String
    val mLimitPicked:ObservableField<Int>  = ObservableField(1)
    var mOptionSelectedIDs:MutableList<String>?=null

    /////////////////////////////////
    /// DB request
    //////////////////////////////////
    fun saveOption(optionStr:String):Boolean {
        if (validInput(optionStr)){
            val option = Option(optionStr , optionStr)
            option.optionType = mOptionType

            commonRepository.saveOption(option)
            return true
        }
        return false
    }

    fun getOptions():LiveData<List<Option>?>{
        return commonRepository.getOptions(AppManager.userID?:"", mOptionType)
    }

    fun addItemSelected(optionID:String){
        if(mLimitPicked.get() != 1){
            if (mOptionSelectedIDs == null)
                mOptionSelectedIDs = ArrayList()
            mOptionSelectedIDs?.add(reformatInput(optionID))
        }
    }

    /////////////////////////////////
    /// support
    //////////////////////////////////
    private fun reformatInput(input:String):String{
        return input.trim()
    }
    private fun validInput(input:String):Boolean{
        if(TextUtils.isEmpty(reformatInput(input)))
            return false
        if (TextUtils.isEmpty(mOptionType)){
            errorMsg.set(application.getString(R.string.invalid_optionType))
            return false
        }
        if (TextUtils.isEmpty(AppManager.userID)){
            errorMsg.set(application.getString(R.string.invalid_userLogged))
            return false
        }
        return true
    }


}