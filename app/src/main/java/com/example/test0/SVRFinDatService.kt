package com.example.test0

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface SVRFinDatService {
    @GET("/data")
    fun getFinData(
        @Query("scope") scope: String
    ) : Call<String>
}