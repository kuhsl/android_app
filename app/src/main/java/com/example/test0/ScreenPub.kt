package com.example.test0

import android.accounts.Account
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.test0.databinding.ActivityPubBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_pub.*
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.URLEncoder


class ScreenPub : AppCompatActivity() {
    val binding by lazy { ActivityPubBinding.inflate(layoutInflater) }

    var AccountList = arrayListOf<ClassPub?>(
//        ClassPub("1995-09-11", "김범중", "본인", "M", "00000","000")
    )

    fun baseUrl(): String {
        if (intent.hasExtra("base_url")) {
            val base_url = intent.getStringExtra("base_url").toString()
            Log.d("tag","base_url:$base_url")
            return base_url
        } else {
            Log.d("tag", "error loading base_url")
            Toast.makeText(this, "No Cookie", Toast.LENGTH_SHORT).show()
            return ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AccountList = arrayListOf(
            getStringArrayPref("public")[0]
        )
//        setStringArrayPref("public", AccountList)
        val scope0 = "public_data"
        val base_url = baseUrl()
        val Adapter = AdapterPub(this, AccountList)
        list_view.adapter = Adapter

        var currCookie = ""
        if (intent.hasExtra("current_cookie")) {
            currCookie = intent.getStringExtra("current_cookie").toString()
            Log.d("tag","cookie_inserted:$currCookie")
        } else {
            Toast.makeText(this, "No Cookie", Toast.LENGTH_SHORT).show()
        }

        CookieManager.getInstance().setCookie(base_url, currCookie)

// Click Listener: when an item on the listview is clicked
        list_view.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->
            var retrofit = ApiClient.getApiClientScalars(base_url, currCookie)

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Action:")
            builder.setPositiveButton("Refresh",
                DialogInterface.OnClickListener{dialog, id ->
                    var refreshService: SVRRefreshService = retrofit.create(SVRRefreshService::class.java)

                    refreshService.requestRefresh(scope0).enqueue(object:
                        Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("tag", "Error: " + t.message)
                            var dialog = AlertDialog.Builder(this@ScreenPub)
                            dialog.setTitle("Error")
                            dialog.setMessage("Process Failed")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("tag", "msg: "+response.body())
                            var dialog = AlertDialog.Builder(this@ScreenPub)
                            dialog.setTitle(response.body())
                            dialog.show()
                        }
                    })
                }
            )
            builder.setNegativeButton("Delete",
                DialogInterface.OnClickListener{dialog, id ->
                    var refreshService: SVRDeleteService = retrofit.create(SVRDeleteService::class.java)

                    refreshService.requestDelete(scope0).enqueue(object:
                        Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("tag", "Error: " + t.message)
                            var dialog = AlertDialog.Builder(this@ScreenPub)
                            dialog.setTitle("Error")
                            dialog.setMessage("Process Failed")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("tag", "msg: "+response.body())
                            var dialog = AlertDialog.Builder(this@ScreenPub)
                            dialog.setTitle(response.body())
                            dialog.show()
                        }
                    })
                }
            )
            builder.show()
        }

        btnGetData.setOnClickListener {
            var retrofit = ApiClient.getApiClientScalars(base_url, currCookie)
            var pubDatService: SVRPubDatService = retrofit.create(SVRPubDatService::class.java)

            pubDatService.getPubData(scope0).enqueue(object:
                Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("tag", "Error: " + t.message)
                    var dialog = AlertDialog.Builder(this@ScreenPub)
                    dialog.setTitle("Error")
                    dialog.setMessage("Process Failed")
                    dialog.show()
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("tag", "msg: "+response.body())
                    Log.d("tag", response.raw().toString())
                    var str = response.body().toString()
                    var data_json = str.split('[')[1].split(']')[0]
                    Log.d("tag", data_json)
                    var data_class = Gson().fromJson(data_json, ClassPub::class.java)
                    setStringArrayPref("public", arrayListOf(data_class))
                    finish();
                }
            })
        }

        btnAddAccount.setOnClickListener {
            CommonLogin(this, currCookie, scope0).show()
        }

        ToHome.setOnClickListener {
            finish()
        }
    }
    fun setStringArrayPref(key: String, values: ArrayList<ClassPub?>) {
        val gson = Gson()
        val json = gson.toJson(values)
        val prefs = getSharedPreferences("public_data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, json)
        editor.apply()
    }
    fun getStringArrayPref(key: String): ArrayList<ClassPub> {
        val prefs = getSharedPreferences("public_data", Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        val gson = Gson()

        val storedData: ArrayList<ClassPub> = gson.fromJson(
            json,
            object : TypeToken<ArrayList<ClassPub?>>() {}.type
        )
        return storedData
    }
}