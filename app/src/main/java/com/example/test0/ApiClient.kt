package com.example.test0

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

object ApiClient {
    fun getApiClientGson(base_url: String, cookie: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .client(provideOkHttpClient(AppInterceptor(cookie)))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    fun getApiClientScalars(base_url: String, cookie: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .client(provideOkHttpClient(AppInterceptor(cookie)))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient
            = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    class AppInterceptor(private val cookie: String) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Cookie", cookie)
                .build()
            proceed(newRequest)
        }
    }
}