package com.example.tataaignewsapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.tataaignewsapp.MockResponseFileReader
import com.example.tataaignewsapp.api.ApiInterface
import com.example.tataaignewsapp.api.DataHandler
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.data.model.NewsResponse
import com.example.tataaignewsapp.di.ApiModule
import com.example.tataaignewsapp.getOrAwaitValue
import com.example.tataaignewsapp.repository.NewsApiRepositoryImpl
import com.example.tataaignewsapp.util.API_KEY
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.net.HttpURLConnection
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull

class HomeViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var apiHelper: NewsApiRepositoryImpl

    @Mock
    private lateinit var articleListObserver: Observer<DataHandler<NewsResponse>?>

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val provideRetrofit = ApiModule.providesRetrofit(ApiModule.providesOkhttpClient())
        apiHelper = NewsApiRepositoryImpl(ApiModule.providesApiInterface(provideRetrofit))

        viewModel = HomeViewModel(apiHelper)
        viewModel.topHeadlines.observeForever(articleListObserver)
        /*viewModel.getTopHeadlines("in",5)*/

        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @Test
    fun `read sample success json file`() {
        val reader = MockResponseFileReader("success-response.json")
        assertNotNull(reader.content)
    }

    @Test
    fun `fetch details and check response Code 200 returned`() {
        runBlocking {
            // Assign
            val response = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockResponseFileReader("success-response.json").content)
            mockWebServer.enqueue(response)
            // Act
            val actualResponse = apiHelper.getTopHeadlines("in", API_KEY, 1, 1)
            // Assert
            assertEquals(
                response.toString().contains("200"),
                actualResponse.code().toString().contains("200")
            )
        }
    }

    @Test
    fun `fetch details and check status Code Ok returned`() {
        runBlocking {
            // Assign
            val response = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockResponseFileReader("success-response.json").content)
            mockWebServer.enqueue(response)
            // Act
            val actualResponse = apiHelper.getTopHeadlines("in", API_KEY, 1, 1)
            // Assert
            assertThat(
                response.getBody().toString().contains("\"status\":\"ok\"") == actualResponse.body()
                    .toString().contains("\"status\":\"ok\"")
            ).isTrue()
        }
    }


    @After
    fun tearDown() {
        viewModel.topHeadlines.removeObserver(articleListObserver)
        mockWebServer.shutdown()
    }
}