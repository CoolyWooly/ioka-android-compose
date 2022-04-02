package kz.ioka.android.ioka.presentation.webView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class WebViewActivity : BaseActivity() {

    companion object {
        fun provideIntent(
            context: Context,
            behavior: WebViewBehavior
        ): Intent {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(LAUNCHER, behavior)

            return intent
        }
    }

    private val viewModel: WebViewViewModel by viewModels {
        WebViewViewModelFactory(
            launcher()!!
        )
    }

    private var launcher: WebViewBehavior? = null

    private lateinit var vToolbar: Toolbar
    private lateinit var webView: WebView
    private lateinit var vProgress: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        launcher = launcher()
        bindViews()
        setupViews()
        observeData()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        webView = findViewById(R.id.webView)
        vProgress = findViewById(R.id.vProgress)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupViews() {
        vToolbar.setNavigationOnClickListener {
            finish()
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url == "https://ioka.kz/") {
                    viewModel.onRedirected()
                }

                return false
            }
        }
    }

    private fun observeData() {
        viewModel.apply {
            vToolbar.setTitle(toolbarTitleRes)
            webView.loadUrl(actionUrl)

            progress.observe(this@WebViewActivity) {
                vProgress.isVisible = it
            }

            result.observe(this@WebViewActivity) {
                setResult(if (it) RESULT_OK else RESULT_CANCELED)
                finish()
            }
        }
    }

}