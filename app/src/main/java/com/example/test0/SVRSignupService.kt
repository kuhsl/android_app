package com.example.test0

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SVRSignupService {
    @FormUrlEncoded
    @POST("/signup")
    fun requestSignup(
        @Field("id") id:String,
        @Field("password") password:String
    ) : Call<String>
}