package com.example.test0

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.test0.databinding.ActivityFinBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_fin.ToHome
import kotlinx.android.synthetic.main.activity_fin.btnAddAccount
import kotlinx.android.synthetic.main.activity_fin.btnGetData
import kotlinx.android.synthetic.main.activity_fin.list_view
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ScreenFin : AppCompatActivity() {
    val binding by lazy { ActivityFinBinding.inflate(layoutInflater) }

    var AccountList = arrayListOf<ClassFin?>(
        ClassFin("001","123456789","100,000")
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
            getStringArrayPref("financial")[0]
        )
        var TransactionList = getStringTXPref("financial")

//        setStringArrayPref("financial", AccountList)
//        setStringTXPref("financial", TransactionList)

        val scope0 = "financial_data"
        val base_url = baseUrl()
        val Adapter = AdapterFin(this, AccountList, TransactionList)
        list_view.adapter = Adapter

        var currCookie = ""
        if (intent.hasExtra("current_cookie")) {
            currCookie = intent.getStringExtra("current_cookie").toString()
            Log.d("tag","cookie_inserted:$currCookie")
        } else {
            Toast.makeText(this, "No Cookie", Toast.LENGTH_SHORT).show()
        }


        CookieManager.getInstance().setCookie(base_url, currCookie)


        btnAddAccount.setOnClickListener {
            CommonLogin(this, currCookie, scope0).show()
        }

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
                            var dialog = AlertDialog.Builder(this@ScreenFin)
                            dialog.setTitle("Error")
                            dialog.setMessage("Process Failed")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("tag", "msg: "+response.body())
                            var dialog = AlertDialog.Builder(this@ScreenFin)
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
                            var dialog = AlertDialog.Builder(this@ScreenFin)
                            dialog.setTitle("Error")
                            dialog.setMessage("Process Failed")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("tag", "msg: "+response.body())
                            var dialog = AlertDialog.Builder(this@ScreenFin)
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
            var finDatService: SVRFinDatService = retrofit.create(SVRFinDatService::class.java)

            finDatService.getFinData(scope0).enqueue(object:
                Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("tag", "Error: " + t.message)
                    var dialog = AlertDialog.Builder(this@ScreenFin)
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
                    var data_class = Gson().fromJson(data_json, ClassFin::class.java)
                    setStringArrayPref("financial", arrayListOf(data_class))

                    var tx_json = str.split('[')[2].split(']')[0]
                    Log.d("tag", tx_json)
                    setStringTXPref("financial", tx_json)

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
    fun setStringArrayPref(key: String, values: ArrayList<ClassFin?>) {
        val gson = Gson()
        val json = gson.toJson(values)
        val prefs = getSharedPreferences("financial_data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, json)
        editor.apply()
    }
    fun getStringArrayPref(key: String): ArrayList<ClassFin> {
        val prefs = getSharedPreferences("financial_data", Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        val gson = Gson()

        val storedData: ArrayList<ClassFin> = gson.fromJson(
            json,
            object : TypeToken<ArrayList<ClassFin?>>() {}.type
        )
        return storedData
    }
    fun setStringTXPref(key: String, value: String) {
        val prefs = getSharedPreferences("transaction_data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun getStringTXPref(key: String): String {
        val prefs = getSharedPreferences("transaction_data", Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)

        return json.toString()
    }
}