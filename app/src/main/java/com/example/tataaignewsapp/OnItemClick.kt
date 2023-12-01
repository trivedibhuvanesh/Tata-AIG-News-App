package com.example.tataaignewsapp

import com.example.tataaignewsapp.data.model.Article
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn

interface OnItemClick {
    fun onArticleClick(article: Article)
}