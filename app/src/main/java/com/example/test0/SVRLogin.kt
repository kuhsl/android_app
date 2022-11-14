package com.example.test0

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.test0.databinding.ActivitySvrloginBinding
import kotlinx.android.synthetic.main.activity_svrlogin.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieHandler
import java.net.CookieManager

class SVRLogin : AppCompatActivity() {
    val binding by lazy { ActivitySvrloginBinding.inflate(layoutInflater) }
    fun dialogFailure(inputObject: String){
        Log.d("tag", "Error: $inputObject")
        var dialog = AlertDialog.Builder(this@SVRLogin)
        dialog.setTitle("Error")
        dialog.setMessage("Process Failed")
        dialog.show()
    }
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

        val base_url = baseUrl()

        var retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        var signupService: SVRSignupService = retrofit.create(SVRSignupService::class.java)
        var loginService: SVRLoginService = retrofit.create(SVRLoginService::class.java)

        btn_login.setOnClickListener {
            var id_input = edit_id.text.toString()
            var pw_input = edit_pw.text.toString()


            loginService.requestLogin(id_input,pw_input).enqueue(object: Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    dialogFailure(t.message.toString())
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("tag", "msg: "+response.body())
                    if (!response.isSuccessful){
                        Log.d("tag", response.raw().toString())
                        dialogFailure(response.body().toString())
                    } else if (!response.body()!!.contains("ERROR", ignoreCase = true)){
                        CookieManager.setDefault(CookieHandler.getDefault())
                        val cook = response.headers()["Set-Cookie"]
                        Log.d("tag", "cookie=" + cook.toString())
                        val loginA = arrayOf(id_input, cook)
                        val intent = Intent()
                        intent.putExtra("returnValue", loginA)
                        setResult(RESULT_OK, intent)
                        Log.d("return intent", intent.toString())
                        finish()
                    } else {
                        //cookie=null 인 경우
                        Log.d("tag", response.raw().toString())
                        dialogFailure(response.body().toString())
                    }
                }
            })
        }

        btn_signup.setOnClickListener {
            var id_input = edit_id.text.toString()
            var pw_input = edit_pw.text.toString()

            signupService.requestSignup(id_input,pw_input).enqueue(object: Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("tag", "Error: " + t.message)
                    var dialog = AlertDialog.Builder(this@SVRLogin)
                    dialog.setTitle("Error")
                    dialog.setMessage("Process Failed")
                    dialog.show()
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("tag", "msg: "+response.body())
                    var dialog = AlertDialog.Builder(this@SVRLogin)
                    dialog.setTitle(response.body())
                    dialog.show()
                }
            })
        }

        toHome.setOnClickListener {
            finish()
        }
    }
}