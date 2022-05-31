package com.example.test0

import retrofit2.Call
import retrofit2.http.*

interface SVRPubDatService {
    @GET("/data")
    fun getPubData(
        @Query("scope") scope: String
    ) : Call<String>
}