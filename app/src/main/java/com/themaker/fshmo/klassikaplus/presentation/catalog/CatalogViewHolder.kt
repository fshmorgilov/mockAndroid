package com.themaker.fshmo.klassikaplus.presentation.catalog

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.themaker.fshmo.klassikaplus.R
import com.themaker.fshmo.klassikaplus.data.domain.Item

class CatalogViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val TAG: String = CatalogViewHolder::class.java.name

    private val view = v
    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var price: TextView

    fun bind(
        item: Item,
        glide: RequestManager,
        onItemClickListener: (Item) -> Unit
    ) {
        image = view.findViewById(R.id.novelty_item_icon)
        name = view.findViewById(R.id.novelty_item_name)
        price = view.findViewById(R.id.novelty_item_price)

        view.setOnClickListener { onItemClickListener(item) }
        Log.d(TAG, "Binded item: ${item.id}")
    }
}

