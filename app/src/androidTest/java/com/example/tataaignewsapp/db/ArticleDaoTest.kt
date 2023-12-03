package com.example.tataaignewsapp.db

import androidx.room.ColumnInfo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.truth.os.BundleSubject
import androidx.test.filters.SmallTest
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.Source
import com.example.tataaignewsapp.util.md5
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import javax.inject.Inject
import javax.inject.Named

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@SmallTest
@HiltAndroidTest
class ArticleDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Inject
    @Named("test_article_db")
    lateinit var articleDatabase: ArticleDatabase
    private lateinit var articleDao: ArticleDao

    @Before
    fun setup(){
        hiltRule.inject()
        articleDao = articleDatabase.getArticleDao()
    }

    @After
    fun tearDown() {
        articleDatabase.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertArticle() {
        runBlockingTest {

            val source = Source(
                id = null,
                name = ""
            )
            val article = Article(
                id = "",
                timestamp = "",
                author = "bhuvanesh",
                content = "tata aig",
                description = "android developer",
                publishedAt = "",
                source = source,
                title = "db test",
                url = "",
                urlToImage = null,
                isBookmarked = false
            )
            val id = md5(
                article.author ?: "",
                article.publishedAt,
                article.title,
            )
            article.id = id

            articleDao.insertArticle(article)
            val getArticle = articleDao.getArticleById(id)
            assertThat(article.author == getArticle.author)
        }
    }

    @Test
    fun getBookmarkedArticleByTitle() {
        runBlockingTest {

            val source = Source(
                id = null,
                name = ""
            )
            val article = Article(
                id = "",
                timestamp = "",
                author = "bhuvanesh",
                content = "tata aig",
                description = "android developer",
                publishedAt = "",
                source = source,
                title = "db test",
                url = "",
                urlToImage = null,
                isBookmarked = true
            )
            val id = md5(
                article.author ?: "",
                article.publishedAt,
                article.title,
            )
            article.id = id

            articleDao.insertArticle(article)
            val getArticle = articleDao.getBookmarkedArticlesByTitle(article.title)
            assertThat(getArticle?.first()?.isBookmarked).isTrue()
        }
    }

}