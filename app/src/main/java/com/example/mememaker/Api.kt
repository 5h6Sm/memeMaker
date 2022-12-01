package com.example.mememaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class MemeData(val explation: String, val url: String, var likes : Int)

interface Api {
    @GET("/memeImg")
    fun getMemeData(): Call<List<MemeData>>
}