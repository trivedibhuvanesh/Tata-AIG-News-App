package com.example.tataaignewsapp.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.tataaignewsapp.R
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.ui.theme.TATAAIGNewsAppTheme
import com.example.tataaignewsapp.util.formatDate
import com.example.tataaignewsapp.util.getCircularLoader
import com.example.tataaignewsapp.util.md5
import com.example.tataaignewsapp.viewmodel.ArticleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        getRandomArticles()

        setContent {
            val context = LocalContext.current
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val screenHeight = configuration.screenHeightDp

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
                                painterResource(id = if(articleViewModel.isBookmarked.value) {
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
                        text = article?.description ?: "",
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

                    if(articleViewModel.randomArticles.isNotEmpty()) {

                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "More Articles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = colorResource(id = R.color.black)
                        )

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            items(articleViewModel.randomArticles) {
                                ChildMoreArticles(context, it, screenWidth, screenHeight)
                            }
                        }
                    }
                }
            }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun ChildMoreArticles(context: Context, item: Article?, screenWidth: Int, screenHeight: Int) {

        Card(
            modifier = Modifier
                .padding(16.dp).shadow(elevation = 16.dp)
        ) {
            Column(modifier = Modifier
                .width(screenWidth.times(0.75).dp)
                .background(colorResource(id = R.color.white),
                    shape = RoundedCornerShape(16.dp)

                ).clickable {
                    val intent = Intent(this@NewsDetailsActivityCompose, NewsDetailsActivityCompose::class.java)
                    intent.putExtra("Article", item)
                    this@NewsDetailsActivityCompose.startActivity(intent)
                    finish()
                }
            ) {

                GlideImage(
                    modifier = Modifier
                        .width(screenWidth.times(0.9).dp)
                        .height(180.dp),
                    model = item?.urlToImage,
                    contentDescription = null,
                    loading = placeholder(getCircularLoader(context)),
                    failure = placeholder(R.drawable.no_image_available_600_x_400),
                    contentScale = ContentScale.Fit
                )

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "${item?.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorResource(id = R.color.black)
                )

                val date = if (item?.publishedAt.isNullOrEmpty().not()) {
                    "Date: ${
                        formatDate(
                            item?.publishedAt ?: "",
                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                            "dd.MM.yyyy 'at' hh:mm a"
                        )
                    }"
                } else {
                    ""
                }
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = date,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )


                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = if (item?.author.isNullOrEmpty().not()){ "Author: ${item?.author}"} else {""} ,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )


                Spacer(modifier = Modifier.padding(bottom = 8.dp))

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

    private fun getRandomArticles() {

        CoroutineScope(Dispatchers.IO).launch {
            val randomArticles = articleViewModel.getRandomArticles(
                11,
            )?.toMutableList()
            randomArticles?.remove(article)
            articleViewModel.randomArticles.clear()
            articleViewModel.randomArticles.addAll(randomArticles ?: mutableListOf())
        }
    }

    private fun openURLInBrowser(url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}
