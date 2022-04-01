package kz.ioka.android.ioka.presentation.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class WebViewActivity : BaseActivity() {

    private var launcher: WebViewLauncher? = null

    private lateinit var vToolbar: Toolbar
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        launcher = launcher()
        bindViews()
        setupViews()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        webView = findViewById(R.id.webView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupViews() {
        vToolbar.title = launcher?.toolbarTitle
        vToolbar.setNavigationOnClickListener {
            finish()
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                setResult(RESULT_OK)
                finish()

                return true
            }
        }
        webView.loadUrl(String.format("%s?returnUrl=https://ioka.kz", launcher?.actionUrl))
    }

}