package com.example.test0

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.test0.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Cookie
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class MainActivity : AppCompatActivity() {

    val base_url = "http://163.152.71.223"

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    object FeedReaderContract {
        // Table contents are grouped together in an anonymous object.
        object FeedEntry : BaseColumns {
            const val TABLE_NAME = "entry"
            const val COLUMN_NAME_TITLE = "title"
            const val ID = "id"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var isLoggedIn = false
        var opID = ""
        var cookie0 = ""

        binding.btnMainFin.setOnClickListener {
            if(!isLoggedIn){
                var dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Login First")
                dialog.show()
            } else {
                val intent = Intent(this, ScreenFin::class.java)
                intent.putExtra("current_cookie", cookie0)
                intent.putExtra("base_url", base_url)
                startActivity(intent)
            }
        }

        binding.btnMainMed.setOnClickListener {
            if(!isLoggedIn){
                var dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Login First")
                dialog.show()
            } else {
                val intent = Intent(this, ScreenMed::class.java)
                intent.putExtra("current_cookie", cookie0)
                intent.putExtra("base_url", base_url)
                startActivity(intent)
            }
        }

        binding.btnMainPub.setOnClickListener {
            if(!isLoggedIn){
                var dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Login First")
                dialog.show()
            } else {
                val intent = Intent(this, ScreenPub::class.java)
                intent.putExtra("current_cookie", cookie0)
                intent.putExtra("base_url", base_url)
                startActivity(intent)
            }
        }

        var getLoginResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
                result -> if (result.resultCode == RESULT_OK){
                val mArray = result.data?.getStringArrayExtra("returnValue")
                opID = mArray?.get(0).toString()
                cookie0 = mArray?.get(1).toString()
                isLoggedIn = true

                Log.d("tag", "Login info : $cookie0")
                var dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("Login Success!")
                dialog.setMessage("ID : $opID")
                dialog.show()

            }
        }

        binding.btnSvrLogin.setOnClickListener {
            if (!isLoggedIn){
                val intent = Intent(this, SVRLogin::class.java)
                intent.putExtra("base_url", base_url)
                getLoginResult.launch(intent)
            }
            else if(isLoggedIn){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("LOGOUT")
                    .setMessage("Do you want to logout? \n ID : $opID")
                    .setPositiveButton("Confirm",
                        DialogInterface.OnClickListener{dialog, id ->
                            opID = ""
                            cookie0 = ""
                            isLoggedIn = false
                        })
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                builder.show()
            }
        }
    }
}