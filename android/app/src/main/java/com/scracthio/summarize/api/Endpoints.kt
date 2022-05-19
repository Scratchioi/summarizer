package com.scracthio.summarize.api

import com.scracthio.summarize.Text
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface Endpoints {
    @POST("convert/")
    fun getSummary(@Body text: Text): Call<Text>

    @Multipart
    @POST("upload/")
    fun getSummaryFromFile(@Part file: MultipartBody.Part, @Part("ext") ext: String): Call<Text>
}