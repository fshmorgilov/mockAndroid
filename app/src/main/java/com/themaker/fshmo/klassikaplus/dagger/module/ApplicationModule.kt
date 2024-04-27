package com.themaker.fshmo.klassikaplus.dagger.module

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.themaker.fshmo.klassikaplus.data.preferences.Preferences
import com.themaker.fshmo.klassikaplus.service.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule () {

    @Provides
    @Singleton
    fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun notificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun localBroadcastManager(@ApplicationContext context: Context): LocalBroadcastManager {
        return LocalBroadcastManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun preferences(sharedPreferences: SharedPreferences?): Preferences {
        return Preferences(sharedPreferences!!)
    }
    @Provides
    @Singleton
    fun context(@ApplicationContext context: Context?): Context {
        return context!!
    }

    @Provides
    @Singleton
    fun networkUtils(@ApplicationContext context: Context?): NetworkUtils{
        return NetworkUtils(context!!)
    }
}