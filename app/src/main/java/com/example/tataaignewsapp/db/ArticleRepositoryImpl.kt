package com.example.tataaignewsapp.db

import com.example.tataaignewsapp.data.model.Article
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(private val articleDao: ArticleDao) :
    ArticleRepository {

    override fun getAllArticles() = articleDao.getAllArticles()

    override suspend fun insertArticle(article: Article) = articleDao.insertArticle(article)
    override suspend fun insertArticleReplace(article: Article) = articleDao.insertArticleReplace(article)

    override suspend fun updateArticle(article: Article) = articleDao.updateArticle(article)

    override suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)

    //override suspend fun deleteArticleById(id: Int) = articleDao.deleteArticleById(id)

    override suspend fun clearArticle() = articleDao.clearArticle()
    override suspend fun getArticleByAuthorPublishDateAndTitle(
        author: String,
        publishedAt: String
    )  =  articleDao.getArticleByAuthorPublishDateAndTitle(author, publishedAt)

    override suspend fun getArticleById(id: String)  =  articleDao.getArticleById(id)

    override suspend fun getBookmarkedArticles(): List<Article?>? = articleDao.getBookmarkedArticles()
    override suspend fun getBookmarkedArticlesByTitle(keyword: String): List<Article?>? = articleDao.getBookmarkedArticlesByTitle(keyword)
    override suspend fun getPagedArticles(limit: Int, offset: Int): List<Article?>? = articleDao.getPagedArticles(limit, offset)
}