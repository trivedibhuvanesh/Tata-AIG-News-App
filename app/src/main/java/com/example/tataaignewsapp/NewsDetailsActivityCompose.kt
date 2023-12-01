package com.example.tataaignewsapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.ui.theme.TATAAIGNewsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest

@AndroidEntryPoint
class NewsDetailsActivityCompose : AppCompatActivity() {

    private val articleViewModel: ArticleViewModel by viewModels()

    private var article: Article? = null
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.extras?.getSerializable("Article") != null) {

            article = intent.extras?.getSerializable("Article") as Article
            article?.id = md5(
                article?.author ?: "",
                article?.publishedAt ?: "",
                article?.title ?: ""
            )
            getArticleByAuthorPublishDateAndTitle(
                article?.author ?: "",
                article?.publishedAt ?: "",
                article?.title ?: ""
            )
        } else {
            finish()
        }

        setObserver()
        setContent {
            val context = LocalContext.current
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            TATAAIGNewsAppTheme {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(textAlign = TextAlign.Center, maxLines = 1, text = "News",color = colorResource(
                            id = R.color.black
                        )) },
                        navigationIcon = {
                            IconButton(onClick = {
                                this@NewsDetailsActivityCompose.finish()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack, contentDescription = "", tint = colorResource(
                                        id = R.color.black
                                    )
                                )
                            }
                        },
                        actions = {
                            Image(
                                painterResource(id = if(articleViewModel.isBookmarked.value == true) {
                                    R.drawable.baseline_bookmark_added_24
                                }
                                else {
                                    R.drawable.outline_bookmark_add_24
                                }),
                                contentDescription = "Bookmark Button",
                                colorFilter = if (articleViewModel.isBookmarked.value) {
                                    ColorFilter.tint(colorResource(id = R.color.colorPrimary))
                                } else {
                                    ColorFilter.tint(colorResource(id = R.color.black))
                                },
                                modifier = Modifier.clickable {
                                    articleViewModel.isBookmarked.value = articleViewModel.isBookmarked.value != true
                                    articleViewModel.insertArticle(article!!)
                                }
                            )
                        },

                        /*colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = Color.White,
                            scrolledContainerColor = Color.White,
                            navigationIconContentColor = Color.Black,
                            titleContentColor = Color.Black,
                            actionIconContentColor = Color.Black,
                        )*/
                    )

                }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {

                    GlideImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = article?.urlToImage,
                        contentDescription = null,
                        loading = placeholder(getCircularLoader(context)),
                        failure = placeholder(R.drawable.no_image_available_600_x_400)
                    )

                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${article?.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.black)
                    )

                    if (article?.publishedAt.isNullOrEmpty().not()) {
                        val date = formatDate(
                            article?.publishedAt ?: "",
                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                            "dd.MM.yyyy 'at' hh:mm a"
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            text = "Date: $date",
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (article?.author.isNullOrEmpty().not()) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            text = "Author: ${article?.author}",
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${article?.description}",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.black)
                    )

                    if (article?.url?.isNotEmpty() == true) {
                        Button(
                            onClick = {
                                openURLInBrowser(article?.url ?: "")
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.colorPrimary)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(text = "Open in Browser", color = colorResource(id = R.color.white))
                        }
                    }
                }
            }}
        }
    }

    private fun setObserver() {
        articleViewModel.articleLiveDate.observe(this) {

            if (it != null) {
                articleViewModel.apply {
                    isBookmarked.value = it.isBookmarked ?: false
                }
                article = it


            } else {
                if(article != null) {
                    articleViewModel.apply {
                        isBookmarked.value = false
                    }
                    article?.isBookmarked = false
                    articleViewModel.insertArticle(article!!)
                }
            }
        }
    }

    private fun getArticleByAuthorPublishDateAndTitle(
        author: String, publishedAt: String, title: String
    ) {
        Log.d("TAG", "getArticleByAuthorPublishDateAndTitle: $author :: $publishedAt")
        //articleViewModel.getArticleByAuthorPublishDateAndTitle(author, publishedAt, title)


        CoroutineScope(Dispatchers.IO).launch {
            val articleInDb =article?.id?.let {
                articleViewModel.getArticleById(it)
            }
            articleViewModel.isBookmarked.value = articleInDb?.isBookmarked ?: false
            if(articleInDb != null) {
                article = articleInDb
            }
        }
    }

    fun md5(author: String, publishedAt: String, title: String): String {
        val input = author+publishedAt+title
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun openURLInBrowser(url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}
