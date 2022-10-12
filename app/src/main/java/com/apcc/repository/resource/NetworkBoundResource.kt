package com.apcc.repository.resource

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apcc.api.*
import com.apcc.framework.AppExecutors
import com.apcc.framework.AppManager
import com.apcc.repository.request.BaseRequest
import com.apcc.vo.Resource


/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 */
abstract class NetworkBoundResource<ResponseType>
@MainThread constructor(apiService: ApiService,
                        private val appExecutors: AppExecutors,
                        request: BaseRequest)
    : BaseApiResource<ResponseType>(apiService, request) {

    private var dbSource:LiveData<ResponseType?>

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        dbSource = loadFromDb()
        if (!request.isSyncRequest()) {
            // do not Request when sync
            appExecutors.diskIO().execute {
                saveCallRequest()
            }
        }
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetchFromNetwork(data)) {
                setValue(Resource.loading(data))
                fetchFromNetwork()
            } else {
                setValue(Resource.success(data))
            }
        }
    }


    private fun fetchFromNetwork() {
        val apiResponse = createCallAPI()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            when (response) {
                is ApiSuccessResponse -> {
                    doSuccessResponse(response)
                }
                is ApiEmptyResponse -> {
                    doEmptyResponse(response)
                }
                is ApiErrorResponse -> {
                    doApiErrorResponse(response)
                }
                is ApiRejectResponse ->{
                    doApiRejectResponse(response)
                }
                is SessionErrorResponse -> {
                    doSessionErrorResponse(response)
                }
            }
        }
    }

    private fun doSuccessResponse(response: ApiSuccessResponse<ApiTransfer>){
        appExecutors.diskIO().execute {
            saveApiResponse(processResponse(response.body))
            appExecutors.mainThread().execute {
                result.addSource(loadFromDb()) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    private fun doEmptyResponse(response: ApiEmptyResponse<ApiTransfer>){
        appExecutors.mainThread().execute {
            // reload from disk whatever we had
            result.addSource(loadFromDb()) { newData ->
                setValue(Resource.success(newData))
            }
        }
    }

    private fun doApiErrorResponse(response: ApiErrorResponse<ApiTransfer>){
        appExecutors.diskIO().execute {
            if (!request.isSyncRequest() ){
                onFetchFailed()
            }
        }
        result.addSource(dbSource) { newData ->
            setValue(Resource.error(response.errorMessage, newData))
        }
    }

    private fun doApiRejectResponse(response: ApiRejectResponse<ApiTransfer>){
        appExecutors.diskIO().execute {
            if ( request.isSyncRequest() ){
                onUpdateReject(processResponse(response.body))
            }else{
                onFetchFailed()
            }
        }
        result.addSource(dbSource) { newData ->
            setValue(Resource.error(response.errorMessage, newData))
        }
    }



    /**
     * error code 401
     */
    private fun doSessionErrorResponse(response: SessionErrorResponse<ApiTransfer>){
        if (!request.isSyncRequest()){
            appExecutors.diskIO().execute {
                onFetchFailed()
            }
        }
        if (requestCleanSession()){
            AppManager.cleanSession()
            AppManager.newTaskStack()
        }else{
            setValue(Resource.error(response.errorMessage, null))
        }
    }


    @MainThread
    protected open fun shouldFetchFromNetwork(data: ResponseType?): Boolean = true

    @MainThread
    protected open fun loadFromDb(): LiveData<ResponseType?>{
        return MutableLiveData(null)
    }

    @MainThread
    protected open fun saveCallRequest(){}

    protected open fun requestCleanSession():Boolean{
        return false // app not login => don't care session
    }
}
