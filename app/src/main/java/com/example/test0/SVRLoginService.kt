package com.example.test0

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SVRLoginService {
    @FormUrlEncoded
    @POST("/login")
    fun requestSignup(
        @Field("id") id:String,
        @Field("password") password:String
    ) : Call<String>
}