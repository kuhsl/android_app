package com.example.test0

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface SVRRefreshService {
    @GET("/refresh")
    fun requestRefresh(
        @Query("scope") scope: String
    ) : Call<String>
}