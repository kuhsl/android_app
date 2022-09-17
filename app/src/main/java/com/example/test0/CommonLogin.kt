package com.example.test0

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.page_webview.*
import java.net.URLEncoder


class CommonLogin(context: Context, private val currCookie: String, private val scope0: String, val interaction: Interaction? = null) : Dialog(context) {

    private lateinit var webView: WebView
    private lateinit var mProgressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.page_webview)
        webView = findViewById(R.id.webView1)
        mProgressBar = findViewById(R.id.progress1)

        previos_btn.setOnClickListener {
            val canGoBack: Boolean = webView.canGoBack()
            if (canGoBack) {
                webView.goBack()
            }
        }
        next_btn.setOnClickListener {
            val canGoForward: Boolean = webView.canGoForward()
            if (canGoForward) {
                webView.goForward()
            }
        }

        //TODO: create public,private key using RSA-2048
        //var pubkey=""
        //var prvkey=""
        //save private key on somewhere

        val targetUrl = "http://163.152.71.223/register"
        CookieManager.getInstance().setCookie(targetUrl, currCookie)

        webView.apply {
            webViewClient = WebViewClientClass()
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    val newWebView = WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(context).apply {
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }


            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true

            // Enable and setup web view cache
            settings.cacheMode =
                WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true
            settings.displayZoomControls = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true  // api 26
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.mediaPlaybackRequiresUserGesture = false
            }

            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.allowUniversalAccessFromFileURLs = true
            }

            settings.allowFileAccess = true
            settings.loadsImagesAutomatically = true

            fitsSystemWindows = true
        }

        //TODO: modify to use postUrl instead of loadUrl
        //val getData = "?scope=${URLEncoder.encode(scope0, "UTF-8")}"
        //postData="key="+URLEncoder.encode(pubkey,"UTF-8")
        //webView.postUrl(targetUrl+loadData,postData.getBytes())

        val loadData = "?scope=${URLEncoder.encode(scope0, "UTF-8")}"
        webView.loadUrl(targetUrl+loadData)
    }

    inner class WebViewClientClass : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mProgressBar.visibility = ProgressBar.VISIBLE
            webView.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            mProgressBar.visibility = ProgressBar.GONE
            webView.visibility = View.VISIBLE
        }


        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            var builder: android.app.AlertDialog.Builder =
                android.app.AlertDialog.Builder(context)
            var message = "SSL Certificate error."
            when (error.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
            }
            message += " Do you want to continue anyway?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue",
                DialogInterface.OnClickListener { _, _ -> handler.proceed() })
            builder.setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
            val dialog: android.app.AlertDialog? = builder.create()
            dialog?.show()
        }
    }
}
//
//import android.annotation.SuppressLint
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.graphics.Rect
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.webkit.*
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.page_webview.*
//import net.gotev.cookiestore.okhttp.JavaNetCookieJar
//import okhttp3.Cookie
//import okhttp3.CookieJar
//import okhttp3.HttpUrl
//import okhttp3.OkHttpClient
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import java.net.CookieHandler
//
//class CommonLogin(context: Context, private val currCookie: String, val interaction: Interaction? = null) : Dialog(context) {
//    private lateinit var webView: WebView
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        val cookieManager = CookieHandler.getDefault()
////        val okHttpClient = OkHttpClient.Builder()
////            .cookieJar(JavaNetCookieJar(cookieManager))
////            .build()
////
////        var retrofit = Retrofit.Builder()
////            .client(okHttpClient)
////            .baseUrl(url)
////            .addConverterFactory(ScalarsConverterFactory.create())
////            .build()
////        var registerService: Register = retrofit.create(Register::class.java)
////        registerService.requestRegister().enqueue(object: Callback<String> {
////            override fun onFailure(call: Call<String>, t: Throwable) {
////                Log.d("tag", "Error: " + t.message)
////            }
////
////            override fun onResponse(call: Call<String>, response: Response<String>) {
////                Log.d("tag", "msg: "+response.body())
////            }
////        })
//
////        setContentView(R.layout.page_webview)
//
//        webView = WebView(context)
//        webView.isVerticalScrollBarEnabled = false
//        webView.isHorizontalScrollBarEnabled = false
//
////        var cookieManager = CookieManager.getInstance()
////
////        cookieManager.setAcceptCookie(true)
////        cookieManager.setAcceptThirdPartyCookies(webView, true)
//
////        CookieManager.getInstance().setCookie("http://192.168.0.3/register", currCookie)
//        val webSettings = webView.settings
//        webSettings.javaScriptEnabled = true
//        webSettings.setSupportZoom(true)
//        webSettings.builtInZoomControls = true
//        webSettings.displayZoomControls = false
//        webSettings.loadWithOverviewMode = true
//        webView.webViewClient = NewWebViewClient()
//
//        webView.loadUrl("http://www.naver.com")
//
//    }
//
//    inner class NewWebViewClient : WebViewClient() {
//
//        override fun shouldOverrideUrlLoading(
//            view: WebView?,
//            request: WebResourceRequest?
//        ): Boolean {
//            view?.loadUrl(request?.url.toString())
//            return false
//        }
//
//        // For API 19 and below
//        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//            Log.d("tag", "url: "+url)
//            view.loadUrl(url)
//            return false
//        }
//
//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
////            url?.let {
////                val uri = Uri.parse(it)
////                //status
////                uri.getQueryParameter("status")?.let{status->
////                    if(status =="fail"){
////                        Toast.makeText(context, "로그인에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
////                        dismiss()
////                    }else if(status=="success"){
////                        uri.getQueryParameter("email")?.let{email->
////                            interaction?.onLoginSuccess(email) //sub rather than email
////                            dismiss()
////                        }
////                    }
////                }
////            }
//            val displayRectangle = Rect()
//            val window = window
//            window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
//
//            // Set height of the Dialog to 90% of the screen
//            val layoutParams = view?.layoutParams
//            layoutParams?.height = (displayRectangle.height() * 0.9f).toInt()
//            view?.layoutParams = layoutParams
//        }
//    }
//
//    override fun onBackPressed() {
//        if (webView.canGoBack()) {
//            webView.goBack()
//        } else {
//            super.onBackPressed()
//        }
//    }
//    /* val binding by lazy { ActivityLoginfinBinding.inflate(layoutInflater) }
//
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         setContentView(binding.root)
//
//         val webView = findViewById<WebView>(R.id.webView)
//         webView.settings.apply {
//             javaScriptEnabled = true
//             domStorageEnabled = true
//         }
//         webView.apply {
//             webViewClient = WebViewClient()
//             webView.loadUrl("https://google.com")
//         }
//
//     }
//     */
//}