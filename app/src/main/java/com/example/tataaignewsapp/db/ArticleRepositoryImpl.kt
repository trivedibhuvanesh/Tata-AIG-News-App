package com.example.tataaignewsapp.db

import com.example.tataaignewsapp.data.model.Article
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(private val articleDao: ArticleDao) :
    ArticleRepository {

    override suspend fun insertArticle(article: Article) = articleDao.insertArticle(article)
    override suspend fun insertArticleReplace(article: Article) = articleDao.insertArticleReplace(article)

    override suspend fun getArticleByAuthorPublishDateAndTitle(
        author: String,
        publishedAt: String
    )  =  articleDao.getArticleByAuthorPublishDateAndTitle(author, publishedAt)

    override suspend fun getArticleById(id: String)  =  articleDao.getArticleById(id)

    override suspend fun getBookmarkedArticles(): List<Article?>? = articleDao.getBookmarkedArticles()
    override suspend fun getBookmarkedArticlesByTitle(keyword: String): List<Article?>? = articleDao.getBookmarkedArticlesByTitle(keyword)
    override suspend fun getPagedArticles(limit: Int, offset: Int): List<Article?>? = articleDao.getPagedArticles(limit, offset)
    override suspend fun getRandomArticles(limit: Int): List<Article?>? = articleDao.getRandomArticles(limit)
}