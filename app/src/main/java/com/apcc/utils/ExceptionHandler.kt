package com.apcc.utils

import java.io.PrintWriter
import java.io.StringWriter

import android.app.Activity
import android.content.Intent
import android.os.Build
import com.apcc.emma.BuildConfig
import com.apcc.data.ErrorTracking
import com.apcc.emma.ui.error.ErrorTrackerActivity
import kotlin.system.exitProcess

class ExceptionHandler(private val myContext: Activity) : Thread.UncaughtExceptionHandler {

	override fun uncaughtException(thread:Thread, exception:Throwable) {
		val errorTracking = ErrorTracking()
		val stackTrace = StringWriter()
		exception.printStackTrace(PrintWriter(stackTrace))
		// error content
		errorTracking.stackTrace = stackTrace.toString()
		// device info
		errorTracking.buildBrand = Build.BRAND
		errorTracking.buildDevice = Build.DEVICE
		errorTracking.model = Build.MODEL
		errorTracking.buildID = Build.ID
		errorTracking.product = Build.PRODUCT
		// build info
		errorTracking.sdkVersion = "${Build.VERSION.SDK_INT}"
		errorTracking.releaseVersion = BuildConfig.VERSION_CODE
		errorTracking.releaseName = BuildConfig.VERSION_NAME
		errorTracking.releaseTime = DateHelper.dateTimeToString(BuildConfig.BUILD_TIME)
		errorTracking.incremental = Build.VERSION.INCREMENTAL

		val intent = Intent(myContext, ErrorTrackerActivity::class.java)
		intent.putExtra(ErrorTrackerActivity.EXTRA_ERROR_TRACKING, errorTracking)
		myContext.startActivity(intent)

		android.os.Process.killProcess(android.os.Process.myPid())
		exitProcess(0)
	}


}
