package com.example.tataaignewsapp.repository

import com.example.tataaignewsapp.api.ApiInterface
import com.example.tataaignewsapp.data.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class NewsApiRepositoryImpl @Inject constructor(val apiInterface: ApiInterface): NewsApiRepository {
    override suspend fun getTopHeadlines(
        country: String,
        apiKey: String,
        pageSize: Int?,
        page: Int?
    ): Response<NewsResponse> = apiInterface.getTopHeadlines(country, apiKey, pageSize, page)

    override suspend fun getEverything(
        apiKey: String,
        pageSize: Int?,
        page: Int?,
        query: String?
    ): Response<NewsResponse> = apiInterface.getEverything(apiKey, pageSize, page, query)

}