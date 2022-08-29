package com.example.test0

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response

data class En3Response(
    @SerializedName("img") val img_encoded : String
)
