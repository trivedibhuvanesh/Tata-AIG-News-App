package com.example.tataaignewsapp.data.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "article_table"/*, primaryKeys = ["author","publishedAt","title"]*/)
data class Article(
    @PrimaryKey
    var id: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var timestamp: String? = "",
    @SerializedName("author")
    val author: String? = "",
    @SerializedName("content")
    val content: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("publishedAt")
    val publishedAt: String = "",
    @SerializedName("source")
    val source: Source?,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("url")
    val url: String?,
    @SerializedName("urlToImage")
    val urlToImage: String?,
    var isBookmarked: Boolean?
): Serializable