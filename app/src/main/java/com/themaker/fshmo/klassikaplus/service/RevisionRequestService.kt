package com.themaker.fshmo.klassikaplus.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.themaker.fshmo.klassikaplus.R
import com.themaker.fshmo.klassikaplus.data.preferences.Preferences
import com.themaker.fshmo.klassikaplus.data.web.catalog.CatalogApi
import com.themaker.fshmo.klassikaplus.presentation.root.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject


class RevisionRequestService(@ApplicationContext private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private var notificationManager: NotificationManager? = null

    @Inject
    lateinit var api: CatalogApi
    @Inject
    lateinit var preferences: Preferences


    override fun doWork(): Result {
        try {
            val serverRevision = api.revision().checkRevision().execute().body()!!.data
            if (serverRevision != null && !preferences.isFirstTimeAppLaunch) {
                if (serverRevision > preferences.revision) {
                    makeNotification()
                    preferences.updateRevision(serverRevision)
                }
                if (serverRevision < preferences.revision) {
                    preferences.updateRevision(serverRevision)
                }
            }
            return Result.success()
        } catch (e: IOException) {
            Log.i(TAG, "doWork: call for API failed")
            return Result.failure()
        }

    }

    private fun makeNotification() {
        val notificationTapIntent = Intent(context, MainActivity::class.java)
        notificationTapIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, REQUEST_INTERVAL, notificationTapIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(applicationContext, "abcde")
            .setSmallIcon(R.drawable.logo_main) // fixme: 2/20/2019 change icon
            .setContentTitle(context.getString(R.string.collection_is_updated))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        if (notificationManager == null)
            notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.notify(notificationId, notification)
    }

    private fun logError(throwable: Throwable) {
        if (throwable is IOException) {
            Log.e(TAG, "logError: " + throwable.message)
        } else
            Log.e(TAG, "logError: stopped unexpectedly : \n" + throwable.message)
    }

    companion object {

        private val TAG = RevisionRequestService::class.java.name

        val WORK_TAG = "Revision_request"

        private val channelId = "KlassikaplusChannel"
        private val notificationId = 123
        val REQUEST_INTERVAL = 24//hours
    }
}
