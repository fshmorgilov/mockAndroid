package com.themaker.fshmo.klassikaplus.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.work.WorkManager
import com.themaker.fshmo.klassikaplus.App
import com.themaker.fshmo.klassikaplus.presentation.root.MainActivity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

import java.util.UUID

import android.content.Context.CONNECTIVITY_SERVICE
import dagger.hilt.android.qualifiers.ApplicationContext

class NetworkUtils() {
    val networkReceiver = NetworkReceiver()
    val notificationTapReceiver = NotificationTapReceiver()
    private val networkState = BehaviorSubject.createDefault(isNetworkAvailable)
    @ApplicationContext
    private lateinit var context: Context

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = context
                .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager ?: return false
            val info = connectivityManager.activeNetworkInfo
            return info != null && info.isConnected
        }

    val onlineNetwork: Single<Boolean>
        get() = networkState.subscribeOn(Schedulers.io())
            .filter { online -> online }
            .firstOrError()

    inner class NetworkReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            networkState.onNext(isNetworkAvailable)
        }
    }

    inner class NotificationTapReceiver : BroadcastReceiver() {
        private val TAG = NotificationTapReceiver::class.java.name
        private var workRequestId: UUID? = null
        val ACTION_TAP = "Notification tapped"

        override fun onReceive(context: Context, intent: Intent) {
            MainActivity.start(context)
            Log.i(TAG, "Starting App")
        }

        fun setWorkRequestId(workRequestId: UUID) {
            this.workRequestId = workRequestId
        }
    }

    companion object {
        private val TAG = NetworkUtils::class.java.name

        init {
            Log.i(TAG, "INIT")
        }

        fun instance(): NetworkUtils = NetworkUtils()

    }
}