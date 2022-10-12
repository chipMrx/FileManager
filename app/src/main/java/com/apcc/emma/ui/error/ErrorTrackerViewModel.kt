package com.apcc.emma.ui.error

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.apcc.data.ErrorTracking
import com.apcc.repository.request.CommonRequest
import com.apcc.repository.response.BaseResponse
import com.apcc.repository.response.CommonResponse
import com.apcc.repository.CommonRepository
import com.apcc.utils.DateHelper
import com.apcc.utils.Logger
import com.apcc.vo.Resource
import com.apcc.vo.Status
import javax.inject.Inject

class ErrorTrackerViewModel @Inject constructor(private val application: Application,
                                                private val commonRepository: CommonRepository) : ViewModel() {

    val errorMsg = ObservableField("")
    val isHideContent  = ObservableField(false)
    val isWaiting = MutableLiveData(false)

    var errorTracking:ErrorTracking? = null

    private val _requestSendError = MutableLiveData<CommonRequest>()
    val resultsSendError: LiveData<Resource<CommonResponse>> = _requestSendError.switchMap { request ->
        commonRepository.sendErrorToServer(request)
    }


    init {
        //isHideContent.set(!BuildConfig.DEBUG)
        isHideContent.set(false)
    }

    fun buildString(errorTracking: ErrorTracking?){
        this.errorTracking = errorTracking
        val errorReport = StringBuilder()
        errorTracking?.let {
            errorReport.append("************ CAUSE OF ERROR ************\n\n")
            errorReport.append(errorTracking.stackTrace)
            errorReport.append("\nCrash at: ")
            errorReport.append(DateHelper.formatToShow(errorTracking.createdDate))
            errorReport.append("\n************ DEVICE INFORMATION ***********\n")
            errorReport.append("Brand: ")
            errorReport.append(errorTracking.buildBrand)

            errorReport.append("\nDevice: ")
            errorReport.append(errorTracking.buildDevice)

            errorReport.append("\nModel: ")
            errorReport.append(errorTracking.model)

            errorReport.append("\nId: ")
            errorReport.append(errorTracking.buildID)

            errorReport.append("\nProduct: ")
            errorReport.append(errorTracking.product)

            errorReport.append("\n************ FIRMWARE ************\n")
            errorReport.append("SDK: ")
            errorReport.append(errorTracking.sdkVersion)

            errorReport.append("\nRelease no: ")
            errorReport.append(errorTracking.releaseVersion)

            errorReport.append("\nRelease name: ")
            errorReport.append(errorTracking.releaseName)

            errorReport.append("\nRelease time: ")
            errorReport.append(DateHelper.formatToShow(errorTracking.releaseTime))

            errorReport.append("\nIncremental: ")
            errorReport.append(errorTracking.incremental)
        }
        errorMsg.set(errorReport.toString())
    }

    fun printLog(){
        Logger.e(errorMsg.get()?:"Not found!")
    }

    fun sendError(errorComment:String){
        errorTracking?.let {err->
            err.errorComment = errorComment
            val errorRequest = CommonRequest()
            errorRequest.error = err
            _requestSendError.value = errorRequest
        }
    }

    /////////////////////////////////
    /// API handle
    //////////////////////////////////
    fun handlerSendError(response:Resource<BaseResponse>):Boolean{
        isWaiting.value = response.status == Status.LOADING
        when(response.status){
            Status.SUCCESS ,
            Status.ERROR -> {
                return true
            }
            Status.LOADING ->{
                // do nothings
            }
        }
        return false
    }
}