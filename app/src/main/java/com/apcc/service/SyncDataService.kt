package com.apcc.service

import android.app.Notification
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.apcc.emma.R
import com.apcc.utils.Constant
import com.apcc.base.service.BaseService


class SyncDataService : BaseService<SyncServiceViewModel>() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notification = NotificationCompat.Builder(this, createNotificationChannel(Constant.CHANNEL_NAME, Constant.CHANNEL_ID))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(getString(R.string.title_appSync))
            .setContentText(getString(R.string.msg_syncing))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(100, 20, true)
            .build()
        notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE

        startForeground(Constant.NOTIFICATION_ID, notification)

        initSync()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        dismissNotifyCallComing(Constant.NOTIFICATION_ID)
        super.onDestroy()
    }


    private fun initSync(){
        viewModel.isSyncFinished.observe(this) { isFinished ->
            if (isFinished) {
                stopSelf()
            }
        }

        viewModel.syncDataLive.observe(this) { syncData ->
            viewModel.startSync(syncData)
        }

        viewModel.resultsUpdateError.observe(this) {
            viewModel.handlerResponseStatus(it)
        }
        viewModel.resultsRemoveFile.observe(this) {
            viewModel.handlerResponseStatus(it)
        }
        viewModel.resultsUpdateFile.observe(this) {
            viewModel.handlerResponseStatus(it)
        }
    }



}