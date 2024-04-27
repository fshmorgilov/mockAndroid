package com.themaker.fshmo.klassikaplus.data.mappers

import android.util.Log
import com.themaker.fshmo.klassikaplus.data.domain.Item
import com.themaker.fshmo.klassikaplus.data.persistence.model.DbItem

class DbToDomainItemMapper : Mapping<DbItem, Item>() {

    override fun map(dbItem: DbItem): Item {
        val item = Item(dbItem.extId)
        item.id = dbItem.id
        Log.d(TAG, "map: Item parsed: $item")
        return item
    }

    companion object {

        private val TAG = DbToDomainItemMapper::class.java.name
    }
}
