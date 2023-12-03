package com.example.tataaignewsapp.repository

import com.example.tataaignewsapp.data.model.NewsResponse
import retrofit2.Response

interface NewsApiRepository {

    suspend fun getTopHeadlines(
        country: String,
        apiKey: String,
        pageSize: Int?,
        page: Int?
    ): Response<NewsResponse>

    suspend fun getEverything(
        apiKey: String,
        pageSize: Int?,
        page: Int?,
        query: String?
    ): Response<NewsResponse>
}