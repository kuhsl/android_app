package com.example.test0

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SVREn1DatService {
    @GET("/engine1")
    fun getEn1Data() : Call<En1Response>
}