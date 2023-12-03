package com.example.tataaignewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tataaignewsapp.data.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertArticleReplace(article: Article)

    @Query("SELECT * FROM article_table WHERE author = :author AND publishedAt = :publishedAt LIMIT 1")
    fun getArticleByAuthorPublishDateAndTitle(author: String, publishedAt: String) : LiveData<Article>

    @Query("SELECT * FROM article_table WHERE id = :id")
    fun getArticleById(id: String) : Article

    @Query("SELECT * FROM article_table WHERE isBookmarked = 1")
    fun getBookmarkedArticles() : List<Article?>?

    @Query("SELECT * FROM article_table WHERE isBookmarked = 1 AND title LIKE '%' || :keyword || '%'")
    fun getBookmarkedArticlesByTitle(keyword: String) : List<Article?>?

    @Query("SELECT * FROM article_table LIMIT :limit OFFSET :offset")
    fun getPagedArticles(limit: Int, offset: Int): List<Article?>?


    @Query("SELECT * FROM article_table ORDER BY RANDOM() LIMIT :limit")
    fun getRandomArticles(limit: Int): List<Article?>?

}