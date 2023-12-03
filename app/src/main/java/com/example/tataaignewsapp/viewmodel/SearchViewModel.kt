package com.example.tataaignewsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tataaignewsapp.util.API_KEY
import com.example.tataaignewsapp.api.DataHandler
import com.example.tataaignewsapp.repository.NewsApiRepositoryImpl
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val networkRepository: NewsApiRepositoryImpl): ViewModel() {

    private val _topHeadlines = MutableLiveData<DataHandler<NewsResponse>>()
    val topHeadlines: LiveData<DataHandler<NewsResponse>> = _topHeadlines
    var searchNewsPage = 1
    var articlesList: MutableList<Article?> = mutableListOf()
    var searchQuery: String? = ""
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var isOffline = false
    suspend fun searchNews(pageSize: Int? ) {
            viewModelScope.launch(Dispatchers.IO) {
                _topHeadlines.postValue(DataHandler.LOADING())
                val response = networkRepository.getEverything(API_KEY, pageSize, searchNewsPage, searchQuery)
                _topHeadlines.postValue(handleResponse(response))
            }
    }

    private fun handleResponse(response: Response<NewsResponse>): DataHandler<NewsResponse> {
        return if (response.isSuccessful) {
            if(response.body() != null) {
                articlesList.addAll(
                    response.body()?.articles?.toMutableList() ?: mutableListOf()
                )
                searchNewsPage++
                DataHandler.SUCCESS(response.body()!!)
            } else {
                DataHandler.ERROR(message = response.errorBody().toString())
            }
        } else {
            DataHandler.ERROR(message = response.errorBody().toString())
        }
    }
}