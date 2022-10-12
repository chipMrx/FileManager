package com.apcc.emma.ui.deeplink

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apcc.repository.response.CommonResponse
import com.apcc.repository.CommonRepository
import com.apcc.vo.Resource
import com.apcc.vo.Status
import javax.inject.Inject

class DeepLinkViewModel  @Inject constructor(private val application: Application,
                                             private val commonRepository: CommonRepository) : ViewModel() {

    val isWaiting = MutableLiveData(true)


}