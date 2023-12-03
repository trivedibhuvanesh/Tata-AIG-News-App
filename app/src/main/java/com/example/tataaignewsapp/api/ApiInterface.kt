package com.example.tataaignewsapp.api

import com.example.tataaignewsapp.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int?,
        @Query("page") page: Int?
    ): Response<NewsResponse>


    @GET("v2/everything")
    suspend fun getEverything(
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int?,
        @Query("page") page: Int?,
        @Query("q") query: String?
    ): Response<NewsResponse>

}