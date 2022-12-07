package com.example.test0

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_analysis_result.*
import kotlinx.android.synthetic.main.activity_pub.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScreenAnalysisResult : AppCompatActivity() {

    fun baseUrl(): String {
        if (intent.hasExtra("base_url")) {
            val base_url = intent.getStringExtra("base_url").toString()
            Log.d("tag","base_url:$base_url")
            return base_url
        } else {
            Log.d("tag", "error loading base_url")
            return ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_result)
        //TODO: modify request address - from operator to platform(engine1, engine2, engine3)
        //val base_url = baseUrl()
        //val base_url = "https://90cbbd1c-3225-4b1a-8d3b-58b50031dd36.mock.pstmn.io"

        Log.d("test#3","timestamp-[start]: "+System.currentTimeMillis())

        val e1_url = "http://163.152.71.223:8084"
        val e2_url = "http://163.152.71.223:8084"
        val e3_url = "http://163.152.71.223:8084"

        loadEn1Data(e1_url)
        loadEn2Data(e2_url)
        loadEn3Data(e3_url)

        Log.d("test#3","timestamp-[end]: "+System.currentTimeMillis())
    }

    fun loadEn1Data(base_url:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val en1dat = retrofit.create(SVREn1DatService::class.java)

        en1dat.getEn1Data().enqueue(object:Callback<En1Response> {
            override fun onFailure(call: Call<En1Response>, t: Throwable) {
                Log.d("tag", "Error: " + t.message)

            }
            override fun onResponse(call: Call<En1Response>, response: Response<En1Response>) {
                Log.d("tag", "msg: "+response.body())

                setEn1Value(response)
            }
        })
    }

    fun loadEn2Data(base_url:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val en2dat = retrofit.create(SVREn2DatService::class.java)

        en2dat.getEn2Data().enqueue(object:Callback<En2Response> {
            override fun onFailure(call: Call<En2Response>, t: Throwable) {
                Log.d("tag", "Error: " + t.message)

            }
            override fun onResponse(call: Call<En2Response>, response: Response<En2Response>) {
                Log.d("tag", "msg: "+response.body())

                setEn2Value(response)
            }
        })
    }

    fun loadEn3Data(base_url:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val en3dat = retrofit.create(SVREn3DatService::class.java)

        en3dat.getEn3Data().enqueue(object:Callback<En3Response> {
            override fun onFailure(call: Call<En3Response>, t: Throwable) {
                Log.d("tag", "Error: " + t.message)

            }
            override fun onResponse(call: Call<En3Response>, response: Response<En3Response>) {
                Log.d("tag", "msg: "+response.body())

                setEn3Value(response)
            }
        })
    }

    fun setEn1Value(items:Response<En1Response>){
        valueE66.setText(items.body()?.valE66)
        valueI10.setText(items.body()?.valI10)
        valueR81.setText(items.body()?.valR81)
    }

    fun setEn2Value(items:Response<En2Response>){
        valueFirstLabelEngine2.setText(items.body()?.label1)
        valueSecondLabelEngine2.setText(items.body()?.label2)
        valueThirdLabelEngine2.setText(items.body()?.label3)
    }

    fun setEn3Value(items:Response<En3Response>){
        val img_bytes= Base64.decode(items.body()?.img_encoded, Base64.DEFAULT)
        val img_decoded= BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.size)
        graphEngine3.setImageBitmap(img_decoded)
    }
}