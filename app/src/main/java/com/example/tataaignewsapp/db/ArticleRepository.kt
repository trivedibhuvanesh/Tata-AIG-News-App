package com.example.tataaignewsapp.db

import androidx.lifecycle.LiveData
import com.example.tataaignewsapp.data.model.Article

interface ArticleRepository {
    fun getAllArticles(): LiveData<List<Article>>
    suspend fun insertArticle(article: Article)
    suspend fun insertArticleReplace(article: Article)
    suspend fun updateArticle(article: Article)
    suspend fun deleteArticle(article: Article)
    //suspend fun deleteArticleById(id: Int)
    suspend fun clearArticle()
    suspend fun getArticleByAuthorPublishDateAndTitle(author: String, publishedAt: String): LiveData<Article>
    suspend fun getArticleById(id: String): Article
    suspend fun getBookmarkedArticles(): List<Article?>?
    suspend fun getBookmarkedArticlesByTitle(keyword: String): List<Article?>?
    suspend fun getPagedArticles(limit: Int, offset: Int): List<Article?>?

}