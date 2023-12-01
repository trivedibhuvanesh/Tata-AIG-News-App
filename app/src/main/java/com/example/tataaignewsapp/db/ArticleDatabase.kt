package com.example.tataaignewsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.SourceConverter

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(SourceConverter::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

    companion object {
        const val DB_NAME = "article_database.db"
    }
}