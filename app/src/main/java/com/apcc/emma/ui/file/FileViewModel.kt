package com.apcc.emma.ui.file

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apcc.framework.CacheManager
import com.apcc.repository.CommonRepository
import com.apcc.repository.request.CommonRequest
import com.apcc.repository.response.CommonResponse
import com.apcc.utils.Logger
import com.apcc.vo.Resource
import com.apcc.vo.Status
import javax.inject.Inject

class FileViewModel  @Inject constructor(private val application: Application,
                                         private val commonRepository: CommonRepository
) : ViewModel() {
    val isWaiting = MutableLiveData(true)

    fun getConfigs(): LiveData<Resource<CommonResponse>> {
        return commonRepository.getServerConfig(CommonRequest())
    }

    fun hasLoadConfigs():Boolean{
        return CacheManager.isHasConfig(application)
    }

    fun handlerGetConfigs(result: Resource<CommonResponse>):Boolean{
        isWaiting.value = result.status == Status.LOADING
        Logger.d("Status " + result.status)
        when(result.status){
            Status.LOADING ->{ return false}
            Status.SUCCESS,
            Status.ERROR -> {
                return true
            }
        }
        return false
    }
}