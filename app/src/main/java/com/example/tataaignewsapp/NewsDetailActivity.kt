package com.example.tataaignewsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tataaignewsapp.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityNewsDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_detail)
    }
}