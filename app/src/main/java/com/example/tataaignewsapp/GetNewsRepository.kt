package com.example.tataaignewsapp

import javax.inject.Inject

class NewsApiRepository @Inject constructor(val apiInterface: ApiInterface) {

    suspend fun getTopHeadlines(country: String, apiKey: String, pageSize: Int?, page: Int?) = apiInterface.getTopHeadlines(country, apiKey, pageSize, page)

    suspend fun searchNews(apiKey: String, pageSize: Int?, page: Int?, query: String?) = apiInterface.getEverything(apiKey, pageSize, page, query)

}