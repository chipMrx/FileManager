package com.apcc.emma.ui.dialog.setting

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.apcc.framework.CacheManager
import com.apcc.repository.CommonRepository
import javax.inject.Inject

class SettingViewModel @Inject constructor(private val application: Application,
                                           private val commonRepository: CommonRepository) : ViewModel() {

    var mTitle:ObservableField<String> = ObservableField("")
    val errorMsg = ObservableField("")

    var timeStamp:ObservableField<Boolean> = ObservableField(false)
    var cameraFrame:ObservableField<Int> = ObservableField()
    var pictureQuality:ObservableField<Int> = ObservableField()
    var cameraTimer:ObservableField<Int> = ObservableField()
    var cameraFlash:ObservableField<Int> = ObservableField()


    /////////////////////////////////
    /// Cache request
    //////////////////////////////////
    fun initCameraSettingData(){
        timeStamp.set(CacheManager.getTimeStamp(application))
        cameraFrame.set(CacheManager.getCameraFrame(application))
        pictureQuality.set(CacheManager.getPictureQuality(application))
        cameraTimer.set(CacheManager.getCameraTimer(application))
        cameraFlash.set(CacheManager.getCameraFlash(application))
    }
    /////////////////////////////////
    /// DB request
    //////////////////////////////////
    fun saveCameraSetting(){
        CacheManager.saveCameraSetting(application,
            timeStamp.get()?:false,
            cameraFrame.get()?:0,
            pictureQuality.get()?:0,
            cameraTimer.get()?:0,
            cameraFlash.get()?:0)
    }


    /////////////////////////////////
    /// support
    //////////////////////////////////

}