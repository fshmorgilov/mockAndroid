package com.themaker.fshmo.klassikaplus

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.facebook.stetho.Stetho
import com.themaker.fshmo.klassikaplus.service.RevisionRequestService
import com.themaker.fshmo.klassikaplus.service.RevisionRequestService.Companion.REQUEST_INTERVAL
import com.themaker.fshmo.klassikaplus.service.RevisionRequestService.Companion.WORK_TAG
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)
        performScheduledWork()
    }

    companion object {
        var instance: App? = null
            private set

        private fun performScheduledWork() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = PeriodicWorkRequest.Builder(
                RevisionRequestService::class.java,
                REQUEST_INTERVAL.toLong(),
                TimeUnit.HOURS
            )
                .addTag(WORK_TAG)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest)
        }
    }
}
