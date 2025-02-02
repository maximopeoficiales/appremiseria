package com.example.app_sudamericana.API

import android.net.Uri
import com.example.app_sudamericana.enviroments.Credentials
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitInstance  private constructor() {
    private val api: Retrofit
    fun <S> createRepository(IRepository: Class<S>?): S {
        return api.create(IRepository)
    }

    fun <T> execute(call: Call<T>): Response<T>? {
        return try {
            call.execute()
        } catch (err: IOException) {
            err.printStackTrace()
            null
        }
    }

    companion object {
        var instance: RetrofitInstance? = null
            get() {
                if (field == null) {
                    field = RetrofitInstance()
                }
                return field
            }
            private set
    }

    init {
        api = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                .addInterceptor(OkHttpProfilerInterceptor())
                .build())
            .baseUrl(Credentials.URL_API)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}