package com.themaker.fshmo.klassikaplus.data.repositories

import android.content.Context
import android.util.Log
import com.themaker.fshmo.klassikaplus.App
import com.themaker.fshmo.klassikaplus.data.domain.Item
import com.themaker.fshmo.klassikaplus.data.mappers.*
import com.themaker.fshmo.klassikaplus.data.persistence.AppDatabase
import com.themaker.fshmo.klassikaplus.data.persistence.model.DbItem
import com.themaker.fshmo.klassikaplus.data.web.catalog.CatalogApi
import com.themaker.fshmo.klassikaplus.data.web.dto.catalog.items.DataDto
import com.themaker.fshmo.klassikaplus.data.web.dto.catalog.items.ItemDto
import com.themaker.fshmo.klassikaplus.data.web.dto.catalog.items.ResponseDto
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject
import javax.inject.Singleton

class CatalogRepository : BaseRepository() {

    lateinit var db: AppDatabase
    lateinit var api: CatalogApi
    lateinit var context: Context

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface ICatalogProviderEntryPoint {
        fun getDatabase(): AppDatabase
        fun getCatalogApi() : CatalogApi
        fun getContext(): Context
    }
    init {
        val entryPoint = EntryPointAccessors.fromApplication(
            context = context,
            entryPoint = ICatalogProviderEntryPoint::class.java
        )
        db = entryPoint.getDatabase()
        api = entryPoint.getCatalogApi()
        context = entryPoint.getContext()
    }

    fun provideByCategoryData(category: Int?): Flowable<List<Item>> {
        val itemDtoDbItemMapper = ListMapping(DtoToDbItemMapper())
        val dbItemDomainListMapper = ListMapping(DbToDomainItemMapper())
        return getItemsFromDbByCategory(category)
    }


    private fun getItemsByCategory(category: Int?): Flowable<List<ItemDto>?> {
        Log.i(TAG, "getItemsByCategory: Requested items by category: " + category!!)
        return api.catalog()
            .getItemsByCategory(category)
            .map { it.data }
            .map { it.items }
            .doOnSuccess { it?.let { this.storeItemsInDb(it) } }
            .toFlowable()
            .subscribeOn(Schedulers.io())
    }

    private fun storeItemsInDb(items: List<ItemDto>) {
        val itemDtoDbItemMapper = ListMapping(DtoToDbItemMapper())
        val dbItems = itemDtoDbItemMapper.map(items)
        db!!.itemDao().insertAll(dbItems)
        Log.i(TAG, "storeItemsInDb: items stored " + items.size)
    }

    private fun getItemsFromDbByCategory(categoryId: Int?): Flowable<List<Item>> {
        val dbItemDomainMapper = ListMapping(DbToDomainItemMapper())
        return db.itemDao().getByCategory(categoryId)
            .map { dbItemDomainMapper.map(it) }
    }

    companion object {

        private val TAG = CatalogRepository::class.java.name

        internal var INSTANCE: CatalogRepository

        init {
            INSTANCE = CatalogRepository()
        }
    }
}
