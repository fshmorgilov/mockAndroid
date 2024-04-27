package com.themaker.fshmo.klassikaplus.presentation.catalog

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.themaker.fshmo.klassikaplus.App
import com.themaker.fshmo.klassikaplus.data.domain.Item
import com.themaker.fshmo.klassikaplus.data.domain.ItemCategory
import com.themaker.fshmo.klassikaplus.data.repositories.CatalogRepository
import com.themaker.fshmo.klassikaplus.presentation.common.State
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@InjectViewState
internal class CatalogPresenter : MvpPresenter<CatalogView>() {

    private val TAG: String = javaClass.name

    @Inject
    lateinit var repository: CatalogRepository

    private var categoryList: List<ItemCategory> = ArrayList()
    private var itemList: List<Item> = ArrayList()
    private var currentCategoryId = 2

    internal fun provideDataset(categoryId: Int) {
        Log.i(TAG, "providing data for category: $categoryId")
        viewState.showState(State.Loading)
        if (currentCategoryId == categoryId && categoryList.isNotEmpty())
            displayData(itemList)
        else {
            val subscribe = repository.provideByCategoryData(categoryId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::displayData,
                    this::displayError
                )
            viewState.addSub(subscribe)
        }
    }

    private fun displayError(throwable: Throwable?) {
        throwable?.let { logError(it) }
        viewState.showState(State.NetworkError)
    }

    private fun logError(throwable: Throwable) {
        Log.e(TAG, "logError: error in dataset: " + throwable.message)
    }

    private fun displayData(items: List<Item>) {
        itemList = items
        Log.i(TAG, "provideDataset: # of items: " + items.size)
        viewState.setDataset(items)
        viewState.showState(State.HasData)
    }


}