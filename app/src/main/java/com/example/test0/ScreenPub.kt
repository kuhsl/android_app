package com.example.test0

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.test0.databinding.ActivityPubBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_pub.*
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.URLEncoder
import java.security.KeyStore
import java.security.spec.MGF1ParameterSpec
import java.util.*
import java.util.Base64.getDecoder
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import kotlin.collections.ArrayList
import kotlin.math.*

class ScreenPub : AppCompatActivity() {
    private val KEYSTORE_INSTANCE_TYPE = "AndroidKeyStore"

    val binding by lazy { ActivityPubBinding.inflate(layoutInflater) }


    fun baseUrl(): String {
        if (intent.hasExtra("base_url")) {
            val base_url = intent.getStringExtra("base_url").toString()
            Log.d("tag", "base_url:$base_url")
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

        val scope0 = "public_data"
        val base_url = baseUrl()
        var currCookie = ""
        if (intent.hasExtra("current_cookie")) {
            currCookie = intent.getStringExtra("current_cookie").toString()
            Log.d("tag", "cookie_inserted:$currCookie")
        } else {
            Toast.makeText(this, "No Cookie", Toast.LENGTH_SHORT).show()
        }

        CookieManager.getInstance().setCookie(base_url, currCookie)

        //operator 측에서 값 가져오고 화면에 뿌림
        //이게 필요없을지도
        //loadPubData(base_url,currCookie,scope0)

        //TODO: test generate key, encrypt, decrypt
        //val keyUtil = RSA2048Test()
        //keyUtil.init()


        // Click Listener: when an item on the listview is clicked
        list_view.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                var retrofit = ApiClient.getApiClientScalars(base_url, currCookie)

                val builder = AlertDialog.Builder(this)
                builder.setMessage("Action:")
                builder.setPositiveButton("Refresh",
                    DialogInterface.OnClickListener { dialog, id ->
                        var refreshService: SVRRefreshService =
                            retrofit.create(SVRRefreshService::class.java)

                        refreshService.requestRefresh(scope0).enqueue(object :
                            Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("tag", "Error: " + t.message)
                                var dialog = AlertDialog.Builder(this@ScreenPub)
                                dialog.setTitle("Error")
                                dialog.setMessage("Process Failed")
                                dialog.show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Log.d("tag", "msg: " + response.body())
                                var dialog = AlertDialog.Builder(this@ScreenPub)
                                dialog.setTitle(response.body())
                                dialog.show()
                            }
                        })
                    }
                )
                builder.setNegativeButton("Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        var refreshService: SVRDeleteService =
                            retrofit.create(SVRDeleteService::class.java)

                        refreshService.requestDelete(scope0).enqueue(object :
                            Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("tag", "Error: " + t.message)
                                var dialog = AlertDialog.Builder(this@ScreenPub)
                                dialog.setTitle("Error")
                                dialog.setMessage("Process Failed")
                                dialog.show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Log.d("tag", "msg: " + response.body())
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
            loadPubData(base_url, currCookie, scope0)
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
        val json = prefs.getString(key, "")
        val gson = Gson()

        val storedData: ArrayList<ClassPub> = gson.fromJson(
            json,
            object : TypeToken<ArrayList<ClassPub?>>() {}.type
        )

        return storedData
    }

    fun loadPubData(base_url: String, currCookie: String, scope0: String) {
        var retrofit = ApiClient.getApiClientScalars(base_url, currCookie)
        var pubDatService: SVRPubDatService = retrofit.create(SVRPubDatService::class.java)

        pubDatService.getPubData(scope0).enqueue(object :
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("tag", "Error: " + t.message)
                var dialog = AlertDialog.Builder(this@ScreenPub)
                dialog.setTitle("Error")
                dialog.setMessage("Process Failed")
                dialog.show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                var str = response.body().toString()

                if (str != null) {
                    var data_json = ""
                    data_json = decryptDat(str, scope0)

                    var data_class = Gson().fromJson(data_json, ClassPub::class.java)
                    setStringArrayPref("public", arrayListOf(data_class)) //받아온 값 저장

                    val Adapter = AdapterPub(this@ScreenPub, arrayListOf(data_class))
                    list_view.adapter = Adapter
                }

            }
        })
    }

    fun decryptDat(passedStr: String, scope0: String): String {
        var encodedJson = passedStr.split('[')[1].split(']')[0]
        val encodedStr = JSONObject(encodedJson).getString("enc_data")
        val encodedByte = Base64.decode(encodedStr, Base64.DEFAULT)
        Log.d("encoded data passed from operator: ", encodedStr)

        //TODO: decode encrypted values
        var decryptedJsonString = ""

        val alias = scope0 + ".keypair"
        val keyStore = KeyStore.getInstance(KEYSTORE_INSTANCE_TYPE).apply {
            load(null)
        }

        if (keyStore.containsAlias(alias)) {
            val keyEntry = keyStore.getEntry(alias, null)
            val ke = keyEntry as KeyStore.PrivateKeyEntry
            val prvkey = ke.privateKey
            val maxLen = 256
            var strLen = encodedByte.size

            for (i in 0 until strLen step maxLen) {
                var end = min(i + maxLen, strLen)

                //println("length of encoded string: ${strLen}, end:${end} ")

                val algorithmParameterSpec = OAEPParameterSpec(
                    "SHA-1",
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
                val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").apply {
                    init(Cipher.DECRYPT_MODE, prvkey, algorithmParameterSpec)
                }


                decryptedJsonString += cipher.doFinal(encodedByte.copyOfRange(i, end)).toString(Charsets.UTF_8)

            }
            decryptedJsonString=decryptedJsonString.split('[')[1].split(']')[0]
            println("decrypted json string: ${decryptedJsonString}")

        }
        return decryptedJsonString
    }
}