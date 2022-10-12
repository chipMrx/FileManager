package com.apcc.emma.ui.file


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.apcc.base.activity.BaseActionBarActivity
import com.apcc.base.activity.BaseActivityVM
import com.apcc.data.FileApp
import com.apcc.emma.R
import com.apcc.framework.AppManager
import com.apcc.utils.FileType
import com.apcc.utils.Util

class FileActivity : BaseActivityVM<FileViewModel>(), FileCallback {

    companion object{

        const val EXTRA_FILE_VIEW = "extraFileView"
        const val EXTRA_FILE_OBJ = "extraFileObj"
        const val EXTRA_FILE_LIST = "extraFileList"
        const val EXTRA_PICKER_TYPE = "extraPickerType" // using FileType
        const val EXTRA_PICKER_MULTIPLE = "extraPickerMultiple" // boolean : single / multiple
    }

    override val resourceLayoutId: Int
        get() = R.layout.file_activity

    override fun onInit() {
        AppManager.instance.flagValid = true
        mNavController = findNavController(R.id.navHostFile)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Util.makeLoveWithGirlFriend(Util.javaClass, Util,  "cappuccino")
        super.onCreate(savedInstanceState)
    }


    override fun transFileDetail(className:String, fileApp: FileApp) {
        mNavController?.currentDestination?.let {
            if (className == (it as FragmentNavigator.Destination).className) {
                val args = Bundle().apply {
                    putParcelable(EXTRA_FILE_OBJ, fileApp)
                    putParcelable(EXTRA_FILE_OBJ, fileApp)
                }
                mNavController?.navigate(R.id.navFileDetail, args)
            }
        }
    }

    override fun transVideoPlayer(className: String, fileApp: List<FileApp>) {
        //TODO("Not yet implemented go to play video")
    }

    override fun transMusicPlayer(className: String, fileApp: List<FileApp>) {
        //TODO("Not yet implemented go to play music")
    }

    override fun transFileLocal(className: String) {
        mNavController?.currentDestination?.let {
            if (className == (it as FragmentNavigator.Destination).className) {
                mNavController?.navigate(R.id.navFolderManager)
            }
        }
    }


    override fun transCamera(className: String) {

    }


    override fun popBack(className: String) {
        super.popBackStack(className)
    }

    override fun returnResultFile(fileApp: FileApp?) {
        if (fileApp != null){
            intent.putExtra(EXTRA_FILE_OBJ, fileApp)
            setResult(RESULT_OK, intent)
        }else{
            setResult(RESULT_CANCELED, intent)
        }
        finish()
    }

    override fun returnResultFile(fileApps: ArrayList<FileApp>) {
        if (fileApps.isEmpty()){
            setResult(RESULT_CANCELED, intent)
        }else{
            intent.putParcelableArrayListExtra(EXTRA_FILE_LIST, fileApps)
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    /////////////////////////////////////////////
    ///
    ///////////////////////////////////////////////

}

