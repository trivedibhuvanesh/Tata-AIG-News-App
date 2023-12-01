package com.example.tataaignewsapp.db

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun providesArticleRepository(articleDao: ArticleDao) = ArticleRepositoryImpl(articleDao)
}