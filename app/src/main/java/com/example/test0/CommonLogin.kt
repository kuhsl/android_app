package com.example.test0

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.room.Fts4
import kotlinx.android.synthetic.main.page_webview.*
import java.io.File
import java.net.URLEncoder
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.KeyPairGenerator


class CommonLogin(context: Context, private val currCookie: String, private val scope0: String, val interaction: Interaction? = null) : Dialog(context) {

    private lateinit var webView: WebView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var pubkey : String
    private lateinit var prvkey : String

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

        val keyUtil = CreateKeyRSA2048(scope0)
        if(keyUtil.init()){
            pubkey=keyUtil.getPubKey()
        }

        if(pubkey=="false") {
            Toast.makeText(this.context, "Generating key pair failed!", Toast.LENGTH_SHORT).show()
            ActivityCompat.finishAffinity(this.context as Activity);
        }else
        {
            Toast.makeText(this.context, "key pair has been generated successfully.", Toast.LENGTH_SHORT).show()
            Log.d("msg", "generated public key: "+pubkey)
        }

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
        val getData = "?scope=${URLEncoder.encode(scope0, "UTF-8")}"
        val postData="pubkey="+pubkey
        webView.postUrl(targetUrl+getData,postData.toByteArray())


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

    private fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)

        return kpg.genKeyPair()
    }
}
