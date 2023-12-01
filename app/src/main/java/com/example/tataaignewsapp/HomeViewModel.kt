package com.example.tataaignewsapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val networkRepository: NewsApiRepository): ViewModel() {

    private val _topHeadlines = MutableLiveData<DataHandler<NewsResponse>>()
    val topHeadlines: LiveData<DataHandler<NewsResponse>> = _topHeadlines
    var searchNewsPage = 1
    var articlesList: MutableList<Article?> = mutableListOf()
    var getTopHeadlinesJob: Job? = null

    fun getTopHeadlines(countryCode: String, pageSize: Int?) {
        getTopHeadlinesJob = viewModelScope.launch(Dispatchers.IO) {
                _topHeadlines.postValue(DataHandler.LOADING())
                val response = networkRepository.getTopHeadlines(countryCode, API_KEY, pageSize, searchNewsPage)
                _topHeadlines.postValue(handleResponse(response))
            }
    }

    private fun handleResponse(response: Response<NewsResponse>): DataHandler<NewsResponse> {
        if (response.isSuccessful) {
            if(response.body() != null) {
                return DataHandler.SUCCESS(response.body()!!)
            } else {
                return DataHandler.ERROR(message = response.errorBody().toString())
            }
        } else {
            return DataHandler.ERROR(message = response.errorBody().toString())
        }
    }
}