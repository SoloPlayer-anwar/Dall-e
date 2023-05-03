package com.twincom.imagegenerateai.network

import com.twincom.imagegenerateai.BuildConfig
import com.twincom.imagegenerateai.utils.Helpers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpClient {

    private var client: Retrofit? = null
    private var endpoint : EndPoint? = null

    companion object {
        private val mInstance: HttpClient = HttpClient()
        @Synchronized
        fun getInstance(): HttpClient {
            return mInstance
        }
    }

    init {
        buildRetrofitClient()
    }

    fun getApi(): EndPoint? {
        if (endpoint == null) {
            endpoint = client!!.create(EndPoint::class.java)
        }
        return endpoint
    }


    private fun buildRetrofitClient() {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(2, TimeUnit.MINUTES)
        builder.readTimeout(2, TimeUnit.MINUTES)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }


        val okHttpClient = builder.build()
        client = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL+"v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Helpers.getDefaultGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        endpoint = null
    }

}