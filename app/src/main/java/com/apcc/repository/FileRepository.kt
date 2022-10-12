package com.apcc.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apcc.api.*
import com.apcc.data.DelaySyncData
import com.apcc.data.FileApp
import com.apcc.repository.request.FileRequest
import com.apcc.repository.response.BaseResponse
import com.apcc.repository.response.FileResponse
import com.apcc.framework.AppExecutors
import com.apcc.framework.db.AppDao
import com.apcc.repository.resource.NetworkBoundResource
import com.apcc.utils.FileHelper
import com.apcc.utils.FileType
import com.apcc.utils.SyncType
import com.apcc.vo.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FileRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val appDao: AppDao,
    private val apiService: ApiService,
    private val application: Application
) {

    /**
     * get list of file
     */
    fun getFile(request: FileRequest): LiveData<Resource<FileResponse>> {
        return object : NetworkBoundResource<FileResponse>(apiService, appExecutors, request) {
            override fun saveApiResponse(item: FileResponse?) {
                item?.listFile?.let {listFile->
                    appDao.insertFiles(listFile)
                }
            }
            override fun loadFromDb(): LiveData<FileResponse?> {
                val data = MutableLiveData<FileResponse?>()
                result.addSource(if (request.fileType == FileType.UNKNOW)
                    appDao.getFiles(request.userID)
                else appDao.getFilesViaType(request.userID, request.fileType)
                ){ listFileApp->
                    val response = FileResponse()
                    response.listFile = listFileApp
                    data.value = response
                }
                return data
            }

            override fun getApiPath() = ApiService.FILE_GET_ALL
        }.asLiveData()
    }

    /**
     * remove file via list ids
     */
    fun removeFiles(request: FileRequest): LiveData<Resource<BaseResponse>> {
        return object : NetworkBoundResource<BaseResponse>(apiService, appExecutors, request) {
            override fun saveApiResponse(item: BaseResponse?) {
                appDao.deleteSync(request.syncID)
            }

            override fun saveCallRequest() {
                 appDao.deleteFile(request.ids)
            }
            override fun getApiPath() = ApiService.FILE_REMOVE
            override fun onUpdateReject(item: BaseResponse?) {
                appDao.deleteSync(request.syncID)
            }

            override fun onFetchFailed() {
                // save remove list to request db
                request.ids?.let {
                    for (id in it) {
                        val delaySyncData = DelaySyncData( id, FileApp::class.java.simpleName, SyncType.DELETE )
                        appDao.requestInsertSync(delaySyncData)
                    }
                }
            }
        }.asLiveData()
    }

    /**
     * make sure data is valid
     */
    fun saveFile(request: FileRequest): LiveData<Resource<FileResponse>> {
        return object : NetworkBoundResource<FileResponse>(apiService, appExecutors, request) {
            override fun saveApiResponse(item: FileResponse?) {
                appDao.deleteSync(request.syncID) // remove sync
                item?.file?.let {file->
                    // remove file in cache after push to server
                    FileHelper.removeFile(FileHelper.getCacheFilePath(application, file))
                    appDao.insertFile(file) // update file info
                }
            }

            override fun loadFromDb(): LiveData<FileResponse?> {
                val data = MutableLiveData<FileResponse?>()
                result.addSource(appDao.getFile(request.id)){ fileApp->
                    val response = FileResponse()
                    if (request.isSyncRequest()){// update for sync
                        request.fileApp = fileApp
                        request.file = FileHelper.getFile(fileApp?.path)
                    }
                    response.file = fileApp
                    data.value = response
                }
                return data
            }

            override fun saveCallRequest() {
                request.fileApp?.let {
                    appDao.insertFile(it)
                }
            }
            override fun getApiPath() = ApiService.FILE_SAVE

            override fun createCallAPI(): LiveData<ApiResponse<ApiTransfer>> {
                if (request.fileApp == null || request.file == null){
                    if (request.isSyncRequest()){
                        return MutableLiveData(ApiRejectResponse( "Data invalid!" , 304))
                    }else{
                        return MutableLiveData(ApiErrorResponse( "Data invalid!" , 0))
                    }
                }
                // file content
                val fileReqBody: RequestBody = request.file!!.asRequestBody("image/*".toMediaTypeOrNull())
                val fileUpload: MultipartBody.Part = MultipartBody.Part.createFormData("fileUpload", request.file!!.name, fileReqBody)
                // file info
                val requestStr = getApiRequestTransData(request).toRequestBody("text/plain".toMediaTypeOrNull())
                return apiService.saveFile(fileUpload, requestStr)// time, sessionID, userID, userInfo)
            }

            override fun onUpdateReject(item: FileResponse?) {
                appDao.deleteSync(request.syncID)
                request.fileApp?.let {
                    appDao.deleteFile(it.fileAppID)
                }
            }

            override fun onFetchFailed() {
                request.let {
                    val delaySyncData = DelaySyncData(request.fileApp!!.fileAppID, FileApp::class.java.simpleName, SyncType.SAVE)
                    appDao.requestInsertSync(delaySyncData)
                }
            }
        }.asLiveData()
    }


    /* *****************************************
    * using for local
    * ******************************************
    *  */
    /**
     * get list of file
     */
    fun getFile(fileID:String):LiveData<FileApp?>{
        return appDao.getFile(fileID)
    }

}