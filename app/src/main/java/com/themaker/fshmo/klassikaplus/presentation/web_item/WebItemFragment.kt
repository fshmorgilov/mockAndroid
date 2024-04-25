package com.themaker.fshmo.klassikaplus.presentation.web_item

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.themaker.fshmo.klassikaplus.R
import com.themaker.fshmo.klassikaplus.data.domain.Item
import com.themaker.fshmo.klassikaplus.presentation.base.MvpBaseFragment
import com.themaker.fshmo.klassikaplus.presentation.common.State
import com.themaker.fshmo.klassikaplus.presentation.root.MainNavigationCallback

class WebItemFragment : MvpBaseFragment() {

    private var item: Item? = null
    private var mainMenuCallback: MainNavigationCallback? = activity as MainNavigationCallback?

    private lateinit var webView: WebView
    private lateinit var error: TextView
    private lateinit var toolbar: Toolbar

    override fun onPostCreateView() {
        super.onPostCreateView()
        initViews()
        setupWebView()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun initViews() {
        with(rootView) {
            webView = findViewById(R.id.web_item_webview)
            error = findViewById(R.id.web_item_error)
            toolbar = findViewById(R.id.web_item_toolbar)
        }
    }

    private fun setupWebView() {
        webView.settings.loadsImagesAutomatically = true
        webView.settings.useWideViewPort = true
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.overScrollMode = View.OVER_SCROLL_NEVER
        showState(State.Loading)
        val bundle = this.arguments
        if (bundle != null) {
            this.item = bundle.getSerializable(KEY_ID) as Item
            Log.d(TAG, "onPostCreateView: item: " + item!!.name)
            if (item != null && item!!.pageAlias != null) {
                webView!!.loadUrl(item!!.pageAlias)
                showState(State.HasData)
                Log.i(TAG, "onPostCreateView: item loaded: " + item!!.pageAlias)
            } else {
                showError("item is null")
            }
        } else {
            showError("bundle is null")
        }
    }

    override fun onDestroy() {
        mainMenuCallback = null
        super.onDestroy()
    }

    private fun showError(message: String) {
        Log.e(TAG, "onPostCreateView: no data provided for webView. Reason: $message")
        showState(State.NoData)
    }

    override fun setLayoutRes(): Int {
        return R.layout.web_item
    }

    override fun onBackPressed() {

    }

    public override fun showState(state: State) {
        Log.i(TAG, "showState: calling state $state")
        when (state) {
            State.HasData -> {
                webView.visibility = View.VISIBLE
                error.visibility = View.GONE
                toolbar.visibility = View.VISIBLE
            }
            State.NoData -> {
                webView.visibility = View.GONE
                error.visibility = View.VISIBLE
                toolbar.visibility = View.VISIBLE
            }
            State.Loading -> {
                webView.visibility = View.GONE
                error.visibility = View.GONE
                toolbar.visibility = View.VISIBLE
            }
            else -> throw IllegalStateException()
        }
    }

    @Deprecated("No longer usable")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mainMenuCallback!!.showMainNavigation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val TAG = WebItemFragment::class.java.name
        private val KEY_ID = "BundleKey"

        fun newInstance(item: Item): WebItemFragment {
            val fragment = WebItemFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_ID, item)
            fragment.arguments = bundle
            Log.i(TAG, "newInstance: fragment created for item: " + item.name)
            return fragment
        }
    }

}
