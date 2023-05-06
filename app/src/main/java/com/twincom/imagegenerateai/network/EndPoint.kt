package com.twincom.imagegenerateai.network

import com.twincom.imagegenerateai.response.OpenAiResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface EndPoint {
    @Multipart
    @POST("images/variations")
    fun submitOpenAi(
        @Header("Authorization") authHeader:String,
        @Part image: MultipartBody.Part,
        @Part("n") number: RequestBody,
        @Part("size") size: RequestBody
    ) : Observable<Response<OpenAiResponse>>
}