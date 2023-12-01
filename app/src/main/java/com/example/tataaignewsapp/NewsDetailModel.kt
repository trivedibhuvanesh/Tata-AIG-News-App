package com.example.tataaignewsapp

import android.graphics.Bitmap
import java.io.Serializable


data class NewsDetailModel(
    var isBookmarked: Boolean? = null,
    val image: Bitmap? = null,
    val newsTitle: String? = null,
    val newsDetail: String? = null,
    val author: String? = null,
    val dateAndTime: String? = null,
    val source: String? = null
): Serializable