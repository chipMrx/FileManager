package com.apcc.api

import android.text.TextUtils
import com.apcc.emma.R
import com.apcc.framework.AppManager
import com.apcc.utils.Logger
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            Logger.i(error.message ?: "Unknown error")
            return ApiErrorResponse(AppManager.instance.getString(R.string.msg_unable), 0)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() != 200) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                        body = body,
                        linkHeader = response.headers()["link"]
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (!TextUtils.isEmpty(response.message())) {
                    response.message()
                } else {
                    msg
                }
                if (response.code() == 401){
                    SessionErrorResponse(errorMsg ?: "Unknown error", response.code())
                }else if (response.code() == 304){
                    ApiRejectResponse(errorMsg ?: "Unknown error", response.code(), response.body())
                }else{
                    ApiErrorResponse(errorMsg ?: "Unknown error", response.code())
                }
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1))
                } catch (ex: NumberFormatException) {
                    Logger.w("cannot parse next page from $next")
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }

    }
}

data class ApiErrorResponse<T>(val errorMessage: String, val errorCode:Int) : ApiResponse<T>()
data class ApiRejectResponse<T>(val errorMessage: String, val errorCode:Int = 304, val body: T ?= null) : ApiResponse<T>()
/* 401 */
data class SessionErrorResponse<T>(val errorMessage: String, val errorCode:Int) : ApiResponse<T>()
