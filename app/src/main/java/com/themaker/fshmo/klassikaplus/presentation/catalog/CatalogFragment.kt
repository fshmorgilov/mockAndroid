package com.themaker.fshmo.klassikaplus.presentation.catalog

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.themaker.fshmo.klassikaplus.R
import com.themaker.fshmo.klassikaplus.data.domain.Item
import com.themaker.fshmo.klassikaplus.data.domain.ItemCategory
import com.themaker.fshmo.klassikaplus.presentation.base.MvpBaseFragment
import com.themaker.fshmo.klassikaplus.presentation.common.State
import com.themaker.fshmo.klassikaplus.presentation.decoration.GridSpaceItemDecoration
import com.themaker.fshmo.klassikaplus.presentation.root.MainNavigationCallback
import com.themaker.fshmo.klassikaplus.presentation.root.WebItemCallback
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.catalog_fragment.*

class CatalogFragment : MvpBaseFragment(), CatalogView {

    private val TAG = javaClass.name
    private val dataset = ArrayList<Item>()

    private lateinit var textError: TextView
    private lateinit var retryBtn: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var recycler: RecyclerView
    private lateinit var webItemCallback: WebItemCallback
    private lateinit var navigationCallback: MainNavigationCallback

    private var currentCategory: ItemCategory = ItemCategory.ZHAKET

    @InjectPresenter
    internal lateinit var presenter: CatalogPresenter

    private lateinit var glide: RequestManager

    override fun onBackPressed() {
        activity?.onBackPressed()
    }

    override fun onPostCreateView() {
        super.onPostCreateView()
        webItemCallback = activity as WebItemCallback
        toolbar = rootView.findViewById(R.id.catalog_toolbar)
        recycler = rootView.findViewById(R.id.catalog_recycler)
        glide = Glide.with(this)
        presenter.provideDataset(currentCategory)
        setupActionBar()
    }

    private fun setupActionBar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar: ActionBar? = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recycler) {
            val catalogAdapter = CatalogAdapter(glide, dataset, webItemCallback::launchItemWebViewFragment)
            layoutManager = LinearLayoutManager(activity)
            catalogAdapter.setDataset(dataset)
            adapter = catalogAdapter
            addItemDecoration(GridSpaceItemDecoration(1, 1))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigationCallback.showMainNavigation()
                return true
            }
            R.id.category_selection -> {
                return true; TODO("open category selection")
            }
            else -> {
                super.onOptionsItemSelected(item); return false
            }
        }
    }

    override fun setDataset(items: List<Item>) {
        dataset.apply {
            if (isEmpty())
                addAll(items)
            else {
                clear(); addAll(items)
            }
            Log.i(TAG, "Setting dataset")
        }
    }

    override fun showError() {
//
    }

    override fun showState(state: State) {
        when (state) {
            State.HasData -> {
                recycler.visibility = View.VISIBLE
                toolbar?.visibility = View.VISIBLE
                textError.visibility = View.GONE
                retryBtn.visibility = View.GONE
            }
            State.Loading -> {
                recycler.visibility = View.GONE
                toolbar?.visibility = View.VISIBLE
                textError.visibility = View.GONE
                retryBtn.visibility = View.GONE
            }
            State.NetworkError -> {
                recycler.visibility = View.GONE
                toolbar?.visibility = View.GONE
                textError.visibility = View.VISIBLE
                retryBtn.visibility = View.VISIBLE
            }
            else -> throw IllegalStateException()
        }
        super.showState(state)
    }

    override fun addSub(subscription: Disposable) {
        super.addSub(subscription)
    }

    override fun setLayoutRes(): Int {
        return R.layout.catalog_fragment
    }
}
