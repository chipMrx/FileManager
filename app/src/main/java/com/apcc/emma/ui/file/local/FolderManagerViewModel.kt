package com.apcc.emma.ui.file.local

import android.Manifest
import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.apcc.data.FileApp
import com.apcc.repository.FileRepository
import com.apcc.utils.FileHelper
import com.apcc.utils.FileType
import java.io.File
import javax.inject.Inject

class FolderManagerViewModel @Inject constructor(private val application: Application,
                                               private val fileRepository: FileRepository) : ViewModel() {
    val errorMsg = ObservableField<String>()

    val isWaiting = MutableLiveData(false)
    val alertMsg = MutableLiveData("")
    val focusPath = MutableLiveData("")

    val resultsGetFile: MutableLiveData<List<FileApp>> = MutableLiveData()

    init {
        getFiles()
    }

    /////////////////////////////////
    /// API request
    //////////////////////////////////

    fun getFiles(filter:Int = FileType.UNKNOW, folder:String = FileHelper.getRootFolder()){
        focusPath.value = folder

        val files:List<File> = if (filter == FileType.UNKNOW){ // get all
            FileHelper.scanAllFile(folder)
        }else{
            FileHelper.filterAllFileViaType(folder, intArrayOf(filter))
        }
        resultsGetFile.postValue(FileHelper.convertListFileToFileApps(files))
    }

    fun removeFiles(files:List<File>){

    }

    /////////////////////////////////
    /// support
    //////////////////////////////////


    fun getRequestPermissionList():List<String>{
        return arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


}