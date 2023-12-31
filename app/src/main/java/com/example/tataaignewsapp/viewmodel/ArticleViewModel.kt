package com.example.tataaignewsapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.db.ArticleRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(private val repository: ArticleRepositoryImpl) : ViewModel() {


    val isBookmarked = mutableStateOf(false)
    var searchKeyword = ""

    var randomArticles = mutableStateListOf<Article?>()


    fun insertArticle(article: Article) {
        article.isBookmarked = isBookmarked.value
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertArticle(article)
        }
    }

    fun insertArticleReplace(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertArticleReplace(article)
        }
    }


    suspend fun getArticleById(id: String) = repository.getArticleById(id)

    suspend fun getBookmarkedArticles() = repository.getBookmarkedArticles()


    suspend fun getBookmarkedArticlesByTitle(keyword: String) = repository.getBookmarkedArticlesByTitle(keyword)

    suspend fun getPagedArticles(limit: Int, offset: Int) = repository.getPagedArticles(limit, offset)

    suspend fun getRandomArticles(limit: Int) = repository.getRandomArticles(limit)

}