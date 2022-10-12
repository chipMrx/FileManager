package com.apcc.emma.ui.file.detail

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.apcc.emma.R
import com.apcc.data.FileApp
import com.apcc.repository.request.FileRequest
import com.apcc.repository.response.BaseResponse
import com.apcc.repository.FileRepository
import com.apcc.utils.DataHelper
import com.apcc.utils.FileType
import com.apcc.vo.Resource
import com.apcc.vo.Status
import javax.inject.Inject

class FileDetailViewModel @Inject constructor(private val application: Application,
                                              private val fileRepository: FileRepository) : ViewModel() {
    val errorMsg = ObservableField<String>()
    val fileContent = ObservableField<String>()
    val fileImageContent = ObservableField<String>()
    val fileJustLogo = ObservableField(false)

    val filePath = ObservableField<String>()
    val fileDate = ObservableField<String>()
    val fileDescription = ObservableField<String>()
    val isWaiting = MutableLiveData(false)
    lateinit var mFileApp:FileApp


    private val _requestRemoveFile = MutableLiveData<FileRequest>()
    val resultsRemoveFile: LiveData<Resource<BaseResponse>> = _requestRemoveFile.switchMap { request ->
        fileRepository.removeFiles(request)
    }


    /////////////////////////////////
    /// API request
    //////////////////////////////////

    fun showFile(fileApp: FileApp?){
        if (fileApp == null){
            errorMsg.set(application.getString(R.string.msg_fileNotFound))
        }else{
            errorMsg.set("")
            mFileApp = fileApp
            filePath.set(application.getString(R.string.lbl_path_s, mFileApp.path))
            fileDate.set(application.getString(R.string.lbl_modifiedDate_s, mFileApp.getDate()))
            fileDescription.set(application.getString(R.string.lbl_description_s, mFileApp.fileDescription))
            when(mFileApp.fileType){
                FileType.DOCUMENT->{
                    fileContent.set("File has content")
                    fileJustLogo.set(false)
                    fileImageContent.set("")
                }
                FileType.IMAGE->{
                    fileContent.set("")
                    fileJustLogo.set(false)
                    fileImageContent.set(DataHelper.formatFileUrl(mFileApp.path))
                }
                FileType.VIDEO->{
                    fileContent.set("")
                    fileJustLogo.set(false)
                    fileImageContent.set(DataHelper.formatFileUrl(mFileApp.path))
                }
                FileType.SOUND->{
                    fileContent.set("")
                    fileJustLogo.set(true)
                    fileImageContent.set("")
                }
                else->{//FileType.UNKNOW
                    fileContent.set("")
                    fileJustLogo.set(true)
                    fileImageContent.set("")
                }
            }
        }

    }

    fun removeFiles(){
        if(::mFileApp.isInitialized){
            val request = FileRequest()
            val fileIDs:MutableList<String> = ArrayList()
            fileIDs.add(mFileApp.fileAppID)
            request.ids = fileIDs
            _requestRemoveFile.value = request
        }
    }

    /////////////////////////////////
    /// API response
    //////////////////////////////////

    fun handlerRemoveFiles(result:Resource<BaseResponse>){
        isWaiting.value = result.status == Status.LOADING
        when(result.status){
            Status.LOADING ->{ errorMsg.set("") }
            Status.SUCCESS -> {
                //getFiles()
            }
            Status.ERROR -> {
                errorMsg.set(result.message)
                //getFiles()
            }
        }
    }
    /////////////////////////////////
    /// support
    //////////////////////////////////



}