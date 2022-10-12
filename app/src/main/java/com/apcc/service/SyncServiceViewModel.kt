package com.apcc.service

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.apcc.data.DelaySyncData
import com.apcc.data.ErrorTracking
import com.apcc.data.FileApp
import com.apcc.framework.AppManager
import com.apcc.framework.CacheManager
import com.apcc.repository.CommonRepository
import com.apcc.repository.FileRepository
import com.apcc.repository.request.CommonRequest
import com.apcc.repository.request.FileRequest
import com.apcc.repository.response.BaseResponse
import com.apcc.repository.response.CommonResponse
import com.apcc.repository.response.FileResponse
import com.apcc.utils.SyncType
import com.apcc.vo.Resource
import com.apcc.vo.Status
import javax.inject.Inject

class SyncServiceViewModel  @Inject constructor(private val application: Application,
                                                private val commonRepository: CommonRepository,
                                                private val fileRepository: FileRepository
                                         ) : ViewModel() {

    private var isWaiting = false
    private var mRequestNextSync:Boolean = false
    val isSyncFinished = MutableLiveData(false)
    val syncDataLive = commonRepository.getSync(AppManager.userID) //MediatorLiveData<List<DelaySyncData>>()

    /*common*/
    private val _requestUpdateError = MutableLiveData<CommonRequest>()
    val resultsUpdateError: LiveData<Resource<CommonResponse>> = _requestUpdateError.switchMap { request ->
        commonRepository.sendErrorToServer(request)
    }
    /*file*/
    private val _requestRemoveFile = MutableLiveData<FileRequest>()
    val resultsRemoveFile: LiveData<Resource<BaseResponse>> = _requestRemoveFile.switchMap { request ->
        fileRepository.removeFiles(request)
    }

    private val _requestUpdateFile = MutableLiveData<FileRequest>()
    val resultsUpdateFile: LiveData<Resource<FileResponse>> = _requestUpdateFile.switchMap { request ->
        fileRepository.saveFile(request)
    }

    /* **** user ****
    * work all time in onl
    * so do not have any thing for syncs
    * */


    init {
    }

    fun handlerResponseStatus(result:Resource<BaseResponse>){
        isWaiting = result.status == Status.LOADING
        when(result.status){
            Status.LOADING ->{}
            Status.SUCCESS,
            Status.ERROR -> {
                //sync finish
                startSync()
            }
        }
    }


    fun cancelSync(){
        mRequestNextSync = false
        // all progress has finish
        if (syncDataLive.value == null && !isWaiting){
            CacheManager.saveSyncFlag(application, false)
            isSyncFinished.postValue(true)
        }
    }

    fun startSync(){
        if (mRequestNextSync) // check exist request for next sync
            startSync(syncDataLive.value)
        else
            cancelSync()
    }

    /**
     * request sync
     * auto re-sync
     */
    fun startSync(syncData:DelaySyncData?){
        if(syncData != null){
            if(AppManager.connection.value == true && !isWaiting){
                mRequestNextSync = false
                when(syncData.tableName){
                    ErrorTracking::class.java.simpleName -> syncError(syncData)
                    FileApp::class.java.simpleName -> syncFile(syncData)
                    // ignore (remove) syncData
                    else -> commonRepository.removeSync(syncData.syncID)
                }
            }else{ // turn on flag request next sync
                mRequestNextSync = true
            }
        }else {// nothing to next sync
            cancelSync()
        }
    }



    //////////////////////////////////////////
    /// request
    /////////////////////////////////////////////
    private fun syncError(dataSync:DelaySyncData){
        val request = CommonRequest()
        request.syncID = dataSync.syncID
        request.id = dataSync.dataID
        request.ids = arrayListOf(dataSync.dataID)

        when (dataSync.requestType){
            SyncType.SAVE->_requestUpdateError.postValue(request)
            // ignore sync
            else->commonRepository.removeSync(dataSync.syncID)
        }
    }

    private fun syncFile(dataSync:DelaySyncData){
        val request = FileRequest()
        request.syncID = dataSync.syncID
        request.id = dataSync.dataID
        request.ids = arrayListOf(dataSync.dataID)

        when (dataSync.requestType){
            SyncType.DELETE->_requestRemoveFile.postValue(request)
            SyncType.SAVE->_requestUpdateFile.postValue(request)
            // ignore sync
            else->commonRepository.removeSync(dataSync.syncID)
        }
    }
}