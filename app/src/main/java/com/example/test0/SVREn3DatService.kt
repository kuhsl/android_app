package com.example.test0

import retrofit2.Call
import retrofit2.http.*

interface SVREn3DatService {
    @GET("/engine3")
    fun getEn3Data() : Call<En3Response>
}