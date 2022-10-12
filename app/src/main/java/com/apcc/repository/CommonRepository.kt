package com.apcc.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apcc.api.ApiService
import com.apcc.data.DelaySyncData
import com.apcc.data.ErrorTracking
import com.apcc.data.Option
import com.apcc.repository.request.CommonRequest
import com.apcc.repository.response.CommonResponse
import com.apcc.framework.AppExecutors
import com.apcc.framework.CacheManager
import com.apcc.framework.db.AppDao
import com.apcc.repository.resource.NetworkBoundResource
import com.apcc.repository.response.BaseResponse
import com.apcc.utils.SyncType
import com.apcc.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val appDao: AppDao,
    private val apiService: ApiService,
    private val application: Application
){

    fun getServerConfig(request: CommonRequest): LiveData<Resource<CommonResponse>> {
        return object : NetworkBoundResource<CommonResponse>(apiService, appExecutors, request){
            override fun saveApiResponse(item: CommonResponse?) {
                item?.let {common->
                    CacheManager.saveConfig(application, common)
                    common.optionList?.let {options->
                        appDao.insertOptions(options)
                    }
                }
            }

            override fun getApiPath() = ApiService.GET_SERVER_CONFIG
        }.asLiveData()
    }

    fun sendErrorToServer(request: CommonRequest): LiveData<Resource<CommonResponse>> {
        return object : NetworkBoundResource<CommonResponse>(apiService, appExecutors, request) {
            override fun saveApiResponse(item: CommonResponse?) {
                appDao.deleteSync(request.syncID)
            }

            override fun getApiPath() = ApiService.SEND_ERRORS

            override fun loadFromDb(): LiveData<CommonResponse?> {
                val data = MutableLiveData<CommonResponse?>()
                result.addSource(appDao.getError(request.id)){ error->
                    val response = CommonResponse()
                    error?.let {
                        if (request.isSyncRequest()){// update for sync
                            request.error = error
                        }
                        response.error = error
                    }
                    data.value = response
                }
                return data
            }

            override fun onUpdateReject(item: CommonResponse?) {
                appDao.deleteSync(request.syncID)
                request.error?.let { error->
                    appDao.deleteError(error.errorID)
                }
            }

            override fun onFetchFailed() {
                // save to sync
                request.error?.let { error->
                    appDao.insertError(error)
                    val delaySyncData = DelaySyncData(error.errorID, ErrorTracking::class.java.simpleName, SyncType.SAVE)
                    appDao.requestInsertSync(delaySyncData)
                }
            }
        }.asLiveData()
    }

    fun saveOption(request: CommonRequest): LiveData<Resource<CommonResponse>> {
        return object : NetworkBoundResource<CommonResponse>(apiService, appExecutors, request) {
            override fun saveApiResponse(item: CommonResponse?) {
                appDao.deleteSync(request.syncID)
            }

            override fun getApiPath() = ApiService.OPTION_SAVE

            override fun loadFromDb(): LiveData<CommonResponse?> {
                val data = MutableLiveData<CommonResponse?>()
                result.addSource(appDao.getOption(request.id)){ option->
                    val response = CommonResponse()
                    option?.let {
                        if (request.isSyncRequest()){// update for sync
                            request.option = option
                        }
                        response.option = option
                    }
                    data.value = response
                }
                return data
            }

            override fun onUpdateReject(item: CommonResponse?) {
                appDao.deleteSync(request.syncID)
                request.option?.let { option->
                    appDao.deleteOption (option.optionID)
                }
            }

            override fun onFetchFailed() {
                // save to sync
                request.option?.let { option->
                    appDao.insertOption(option)
                    val delaySyncData = DelaySyncData(option.optionID, Option::class.java.simpleName, SyncType.SAVE)
                    appDao.requestInsertSync(delaySyncData)
                }
            }
        }.asLiveData()
    }

    fun removeOptions(request: CommonRequest): LiveData<Resource<BaseResponse>> {
        return object : NetworkBoundResource<BaseResponse>(apiService, appExecutors, request) {
            override fun saveApiResponse(item: BaseResponse?) {
                appDao.deleteSync(request.syncID)
            }

            override fun saveCallRequest() {
                appDao.deleteOption(request.ids)
            }

            override fun getApiPath() = ApiService.OPTION_DELETE

            override fun onUpdateReject(item: BaseResponse?) {
                appDao.deleteSync(request.syncID)
            }

            override fun onFetchFailed() {
                request.ids?.let {
                    for (id in it) {
                        val delaySyncData = DelaySyncData(id, Option::class.java.simpleName, SyncType.DELETE)
                        appDao.requestInsertSync(delaySyncData)
                    }
                }
            }
        }.asLiveData()
    }


    fun saveLanguage(request: CommonRequest): LiveData<Resource<CommonResponse>> {
        return object : NetworkBoundResource<CommonResponse>(apiService, appExecutors, request) {

            override fun getApiPath() = ApiService.LANGUAGE_SAVE

        }.asLiveData()
    }

    /* *****************************************
    * using for local
    * ******************************************
    *  */


    fun getSync(userID: String?): LiveData<DelaySyncData?>{
        return appDao.getOldestSync(userID)
    }

    fun removeSync(syncID:String){
        appDao.deleteSync(syncID)
    }

    fun saveOption(option: Option){
        appExecutors.diskIO().execute{
            appDao.insertOption(option)
        }
    }

    fun getOptions(userID:String, optionType:String): LiveData<List<Option>?> {
        return appDao.getOptions(userID, optionType)
    }
}