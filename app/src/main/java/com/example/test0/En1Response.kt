package com.example.test0

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response

data class En1Response(
    @SerializedName("E66") val valE66 : String,
    @SerializedName("I10") val valI10 : String,
    @SerializedName("R81") val valR81 : String
    )
