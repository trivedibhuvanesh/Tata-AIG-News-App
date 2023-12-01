package com.example.tataaignewsapp.di

import android.content.Context
import androidx.room.Room
import com.example.tataaignewsapp.db.ArticleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesArticleDatabase(@ApplicationContext context: Context): ArticleDatabase {
        return Room.databaseBuilder(context, ArticleDatabase::class.java, ArticleDatabase.DB_NAME).build()
    }

    @Singleton
    @Provides
    fun providesArticleDao(articleDatabase: ArticleDatabase) = articleDatabase.getArticleDao()
}