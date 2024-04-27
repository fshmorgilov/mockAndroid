package com.themaker.fshmo.klassikaplus.data.domain

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Item(var id: String?) : Serializable, Parcelable {
    override fun toString(): String {
        return "Item{" +
                "id='" + id + '\''.toString() +
                '}'.toString()
    }

    constructor(
        extId: String?,
        name: String?,
        description: String?,
        vendorCode: String?,
        novelty: Boolean,
        descriptionLong: String?,
        pageAlias: String?,
        icon: String?,
        photo: String?,
        manufacturer: String?,
        basePrice: Double,
        discount: Double,
        discountable: Boolean,
        price: Double,
        category: String?,
        categoryId: Int
    ) : this(extId)

    constructor(source: Parcel) : this(
        source.readString(),
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Item> = object : Parcelable.Creator<Item> {
            override fun createFromParcel(source: Parcel): Item = Item(source)
            override fun newArray(size: Int): Array<Item?> = arrayOfNulls(size)
        }
    }
}

