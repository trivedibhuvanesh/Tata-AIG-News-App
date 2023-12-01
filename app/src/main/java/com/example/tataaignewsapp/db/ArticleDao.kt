package com.example.tataaignewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tataaignewsapp.data.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //if some data is same/conflict, it'll be replace with new data.
    fun insertArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertArticleReplace(article: Article)

    @Update
    fun updateArticle(article: Article)

    @Delete
    fun deleteArticle(article: Article)

    @Query("SELECT * FROM article_table ORDER BY timestamp DESC")
    fun getAllArticles(): LiveData<List<Article>>
    // why not use suspend ? because Room does not support LiveData with suspended functions.
    // LiveData already works on a background thread and should be used directly without using coroutines

    @Query("DELETE FROM article_table")
    fun clearArticle()

/*    @Query("DELETE FROM article_table WHERE id = :id") //you can use this too, for delete article by id.
    fun deleteArticleById(id: Int)*/

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

}