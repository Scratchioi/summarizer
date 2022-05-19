package com.scracthio.summarize.api

import android.util.Log
import com.scracthio.summarize.Text
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit


class ApiClass {
    private var retrofit: Retrofit
    private var endpoints: Endpoints
    private val url = "http://192.168.1.8:8000/"

    init {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(Duration.ZERO)
            .build()

        retrofit = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        endpoints = retrofit.create(Endpoints::class.java)
    }

    fun getSummary(text: String, onComplete: ApiInterface) {
        Log.d("summary", text)
        val call = endpoints.getSummary(Text(text))
        call.enqueue(
            object : Callback<Text> {
                override fun onResponse(call: Call<Text>, response: Response<Text>) {
                    try {
                        Log.d("Summary", "${response.body()} ${response.isSuccessful} ${response.errorBody()}")
                        response.body()
                            ?.let { onComplete.onSuccess(REQUEST_SUCCESSFUL, response.body()!!) }
                    } catch (e: JSONException) {
                        e.message?.let { Log.d("summary!", it) }
                    }
                }

                override fun onFailure(call: Call<Text>, t: Throwable) {
//                    Log.d("summary", t.stackTrace!!)
                    t.printStackTrace()
                    onComplete.onFailure(REQUEST_UNSUCCESSFUL)
                }
            }
        )
    }

    fun getSummaryFromFile(part_file: String, ext: String, onComplete: ApiInterface) {
        val file = File(part_file) // Create a file using the absolute path of the image
        val reqBody: RequestBody =
            RequestBody.create("multipart/form-file".toMediaTypeOrNull(), file)
        val partFile = MultipartBody.Part.createFormData("file", file.name, reqBody)
        val upload: Call<Text> = endpoints.getSummaryFromFile(partFile, ext)
        upload.enqueue(object : Callback<Text?> {
            override fun onResponse(call: Call<Text?>, response: Response<Text?>) {
                if (response.isSuccessful) {
                    Log.d("Api", response.body().toString())
                    onComplete.onSuccess(REQUEST_SUCCESSFUL, response.body()!!)
                }
                else {
                    onComplete.onFailure(REQUEST_UNSUCCESSFUL)
                }
            }
//
            override fun onFailure(call: Call<Text?>, t: Throwable) {
                Log.d("Api", t.message.toString())
                onComplete.onFailure(REQUEST_UNSUCCESSFUL)
            }
        })
    }

    companion object {
        const val REQUEST_SUCCESSFUL: Int = 1
        const val REQUEST_UNSUCCESSFUL: Int = 0
    }
}