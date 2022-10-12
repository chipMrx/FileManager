package com.apcc.api

import com.apcc.utils.Logger
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor() : Interceptor {

    private var headerVal:String = ""
    private var headerKey:String = ""

    init {
        Logger.d("init")
        setVM()
        headerVal = Credentials.basic("AnPhatCS", "AP@12345678")
        headerKey = "Authorization"
    }


    fun initAuth(value1:String, value2:String, value3:String){
        Logger.d("initAuth")
        headerVal = Credentials.basic(value1, value2)
        headerKey = value3
    }

    override fun intercept(chain: Interceptor.Chain):Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header(headerKey, headerVal).build()
        return chain.proceed(authenticatedRequest)
    }

    external fun setVM()
}
