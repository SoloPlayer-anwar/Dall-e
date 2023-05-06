package com.twincom.imagegenerateai.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class OpenAiResponse(
    @SerializedName("created")
    @Expose
    val created: Int,
    @SerializedName("data")
    @Expose
    val `data`: List<Data>
)