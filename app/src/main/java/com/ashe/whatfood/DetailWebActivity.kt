package com.ashe.whatfood

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.webkit.GeolocationPermissions.Callback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.databinding.ActivityDetailwebBinding
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import retrofit2.http.Url

class DetailWebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailwebBinding
    private var checkErrorTime: Long = 0
    private var lastFailedUrl = ""
    var mChildWebView: WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detailweb)

        var url = ""
        if(intent.hasExtra("url")){
            url = intent.getStringExtra("url")!!
        }
        binding.webview.webViewClient = InAppWebViewClient()
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.allowFileAccess = true
        binding.webview.settings.builtInZoomControls = true
        binding.webview.settings.loadWithOverviewMode = true
        binding.webview.settings.useWideViewPort = true
        binding.webview.settings.displayZoomControls = false

        binding.webview.loadUrl(url)
    }

    inner class InAppWebViewClient : WebViewClient() {
        override fun onReceivedSslError(
            view: WebView,
            handler: SslErrorHandler,
            error: SslError
        ) {
            val builder = AlertDialog.Builder(this@DetailWebActivity)
            builder.setMessage("SSL 페이지 오류 입니다.\n에러코드" + error.primaryError)
            builder.setNegativeButton(
                "확인"
            ) { dialog: DialogInterface?, which: Int -> handler.cancel() }
            val dialog = builder.create()
            dialog.show()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            CookieManager.getInstance().flush()
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            val url = request.url.toString()
            Log.d(
                "MainActivity",
                "onReceivedError : $url - ${error.errorCode} + ${error.description}"
            )

            if (url.indexOf("woff") > 0) {
                return
            }

            if (url.contains("billycar") && error.errorCode < 0) {
                if (url != lastFailedUrl) { // 실패 URL 이 같지않다면
                    //  10000 > 2000
                    if (System.currentTimeMillis() > checkErrorTime + 10000) { // 실패 한 URL 이 같지않고 최근 ReLoad 실패한지 10초가 넘었다면
                        checkErrorTime = System.currentTimeMillis()
                        lastFailedUrl = url
                        Log.d("테스트:", "리로드 시도")
                    }
                }
            }

            super.onReceivedError(view, request, error)
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }
    }

}