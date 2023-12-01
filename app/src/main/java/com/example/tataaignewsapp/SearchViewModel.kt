package com.example.tataaignewsapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val networkRepository: NewsApiRepository): ViewModel() {

    private val _topHeadlines = MutableLiveData<DataHandler<NewsResponse>>()
    val topHeadlines: LiveData<DataHandler<NewsResponse>> = _topHeadlines
    var searchNewsPage = 1
    var articlesList: MutableList<Article?> = mutableListOf()
    var searchQuery: String? = ""

    suspend fun searchNews(pageSize: Int? ) {
            //viewModelScope.launch(Dispatchers.IO) {
                _topHeadlines.postValue(DataHandler.LOADING())
                val response = networkRepository.searchNews(API_KEY, pageSize, searchNewsPage, searchQuery)
                _topHeadlines.postValue(handleResponse(response))
            //}
    }

    private fun handleResponse(response: Response<NewsResponse>): DataHandler<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return DataHandler.SUCCESS(it)
            }
        }
        return DataHandler.ERROR(message = response.errorBody().toString())
    }
}