package com.apcc.repository.resource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.apcc.api.ApiResponse
import com.apcc.api.ApiService
import com.apcc.api.ApiTransfer
import com.apcc.repository.request.BaseRequest
import com.apcc.utils.Logger
import com.apcc.utils.RateLimiter
import com.apcc.vo.Resource
import com.google.gson.GsonBuilder
import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit


/**
 * #ResultType : Data we filter from response
 * #ResponseType : Response from api, all of data here
 */
abstract class BaseApiResource<ResponseType>(protected val apiService: ApiService,
                                             protected val request: BaseRequest) {
    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    private val mEncryption = Encryption()
    protected val result = MediatorLiveData<Resource<ResponseType>>()

    val listRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)
    fun getApiRequest(dataRequest: Any?): ApiTransfer {
        val data = gson.toJson(dataRequest)
        Logger.d("data : $data" )
        return ApiTransfer(mEncryption.encrypt3Des(data))
    }
    fun getApiRequestTransData(dataRequest: Any?):String{
        val data = gson.toJson(dataRequest)
        Logger.d("data : $data")
        return mEncryption.encrypt3Des(data)?:""
    }

    open fun getApiResponse(dataResponse:ApiTransfer?):String?{
        val data = mEncryption.decrypt3Des(dataResponse?.bodyData)
        Logger.d(data)
        return data
    }



    //////////////////////////////
    ///
    ///////////////////////////////

    @MainThread
    protected fun setValue(newValue: Resource<ResponseType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResponseType>>


    @MainThread
    open fun onFetchFailed() {}

    /**
     * *** do one by one ***
     *
     */
    @MainThread
    open fun onUpdateReject(item: ResponseType?) {}

    @MainThread
    open fun saveApiResponse(item: ResponseType?){}

    abstract fun getApiPath():String

    @MainThread
    open fun createCallAPI(): LiveData<ApiResponse<ApiTransfer>>{
        return apiService.postRequest(getApiRequest(request), ApiService.getUrl(getApiPath()))
    }

    @WorkerThread
    open fun processResponse(response: ApiTransfer?) :ResponseType?{
        response?.let {
            val bodyData = getApiResponse(response)
            bodyData?.let {
                if (it.isNotEmpty()){
                    return gson.fromJson(it, getResponseTypeClass())
                }
            }
        }
        return null
    }


    @Suppress("UNCHECKED_CAST")
    private fun getResponseTypeClass(): Class<ResponseType> {// 0: ResponseType
        return ((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<ResponseType>)
    }
}