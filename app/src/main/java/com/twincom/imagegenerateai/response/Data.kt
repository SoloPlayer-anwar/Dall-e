package com.twincom.imagegenerateai.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Data(
    @SerializedName("url")
    @Expose
    val url: String
)