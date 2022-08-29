package com.example.test0

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SVREn2DatService {
    @GET("/engine2")
    fun getEn2Data() : Call<En2Response>
}