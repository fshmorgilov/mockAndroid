package com.themaker.fshmo.klassikaplus.dagger.module

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.themaker.fshmo.klassikaplus.data.persistence.AppDatabase
import com.themaker.fshmo.klassikaplus.data.repositories.CatalogRepository
import com.themaker.fshmo.klassikaplus.data.web.catalog.CatalogApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Предоставляет зависимость для БД.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private const val DATABASE_NAME = "klassikaplus.db" // FIXME: 5/13/2019
    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application): AppDatabase {
        return databaseBuilder(
            application,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun proviceCatalogRepository(db: AppDatabase?, api: CatalogApi?): CatalogRepository {
        return CatalogRepository()
    }

    @Provides
    @Singleton
    fun provideCatalogApi(): CatalogApi {
        return CatalogApi.getInstance()
    }
}