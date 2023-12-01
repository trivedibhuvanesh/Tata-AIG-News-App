package com.example.tataaignewsapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.ColumnInfo
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.Source
import com.example.tataaignewsapp.db.ArticleRepositoryImpl
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(private val repository: ArticleRepositoryImpl) : ViewModel() {


    val isBookmarked = mutableStateOf(false)

    private val _article = MutableLiveData<Article?>()
    val articleLiveDate: LiveData<Article?>
        get() = _article

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

    fun updateArticle(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateArticle(article)
        }
    }

    suspend fun deleteArticle(article: Article) = repository.deleteArticle(article)

    //suspend fun deleteArticleById(id: Int) = repository.deleteArticleById(id)

    suspend fun clearArticle() = repository.clearArticle()

    fun getAllArticles() = repository.getAllArticles()

    suspend fun getArticleById(id: String) = repository.getArticleById(id)

    suspend fun getBookmarkedArticles() = repository.getBookmarkedArticles()


    suspend fun getBookmarkedArticlesByTitle(keyword: String) = repository.getBookmarkedArticlesByTitle(keyword)

    suspend fun getPagedArticles(limit: Int, offset: Int) = repository.getPagedArticles(limit, offset)

}