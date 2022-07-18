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
import com.example.test0.databinding.ActivityMedBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_med.*
import kotlinx.android.synthetic.main.activity_med.ToHome
import kotlinx.android.synthetic.main.activity_med.btnAddAccount
import kotlinx.android.synthetic.main.activity_med.btnGetData
import kotlinx.android.synthetic.main.activity_med.list_view
import kotlinx.android.synthetic.main.activity_pub.*
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ScreenMed : AppCompatActivity() {

    val binding by lazy { ActivityMedBinding.inflate(layoutInflater) }

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

        val scope0 = "medical_data"
        val base_url = baseUrl()
        var currCookie = ""
        if (intent.hasExtra("current_cookie")) {
            currCookie = intent.getStringExtra("current_cookie").toString()
            Log.d("tag","cookie_inserted:$currCookie")
        } else {
            Toast.makeText(this, "No Cookie", Toast.LENGTH_SHORT).show()
        }

        CookieManager.getInstance().setCookie(base_url, currCookie)

        //load and set data
        loadMedData(base_url,currCookie,scope0)

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
                            var dialog = AlertDialog.Builder(this@ScreenMed)
                            dialog.setTitle("Error")
                            dialog.setMessage("Process Failed")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("tag", "msg: "+response.body())
                            var dialog = AlertDialog.Builder(this@ScreenMed)
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
                            var dialog = AlertDialog.Builder(this@ScreenMed)
                            dialog.setTitle("Error")
                            dialog.setMessage("Process Failed")
                            dialog.show()
                        }
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("tag", "msg: "+response.body())
                            var dialog = AlertDialog.Builder(this@ScreenMed)
                            dialog.setTitle(response.body())
                            dialog.show()
                        }
                    })
                }
            )
            builder.show()
        }

        btnGetData.setOnClickListener {
            loadMedData(base_url, currCookie, scope0)
        }

        btnAddAccount.setOnClickListener {
            CommonLogin(this, currCookie, scope0).show()
        }

        ToHome.setOnClickListener {
            finish()
        }
    }
    fun setStringArrayPref(key: String, values: ArrayList<ClassMed?>) {
        val gson = Gson()
        val json = gson.toJson(values)
        val prefs = getSharedPreferences("medical_data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, json)
        editor.apply()
    }
    fun getStringArrayPref(key: String): ArrayList<ClassMed> {
        val prefs = getSharedPreferences("medical_data", Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        val gson = Gson()

        val storedData: ArrayList<ClassMed> = gson.fromJson(
            json,
            object : TypeToken<ArrayList<ClassMed?>>() {}.type
        )
        return storedData
    }
    fun loadMedData(base_url: String, currCookie: String, scope0: String){
        var retrofit = ApiClient.getApiClientScalars(base_url, currCookie)
        var medDatService: SVRMedDatService = retrofit.create(SVRMedDatService::class.java)

        medDatService.getMedData(scope0).enqueue(object:
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("tag", "Error: " + t.message)
                var dialog = AlertDialog.Builder(this@ScreenMed)
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
                var data_class = Gson().fromJson(data_json, ClassMed::class.java)
                setStringArrayPref("medical", arrayListOf(data_class)) //받아온 값 저장

                val Adapter = AdapterMed(this@ScreenMed, arrayListOf(data_class))
                list_view.adapter = Adapter
            }
        })
    }
}