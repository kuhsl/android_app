package com.example.test0

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response

data class En2Response(
    @SerializedName("label1") val label1 : String,
    @SerializedName("label2") val label2 : String,
    @SerializedName("label3") val label3 : String
)
