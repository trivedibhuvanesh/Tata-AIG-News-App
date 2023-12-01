package com.example.tataaignewsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tataaignewsapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val articleViewModel: ArticleViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private var limit = 5
    private var offset = 0
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var isOffline = false
    private var job: Job? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                if(isOffline) {
                    getPagedArticles(limit,offset)
                } else {

                    callNewsApi()
                }
                isScrolling = false
            } else {
                binding.rvNews.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setRecyclerView()
        callNewsApi()
        setObserver()
        return binding.root
    }

    private fun callNewsApi() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            yield()
            homeViewModel.getTopHeadlinesJob?.cancel()
            if(isInternetOn(requireContext())) {
                homeViewModel.getTopHeadlines(
                    "in", QUERY_PAGE_SIZE
                )
            } else {
                getPagedArticles(limit, offset)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<TextView>(R.id.tv_current_header).text = "Top Headlines"
    }

    private fun setObserver() {
        homeViewModel.topHeadlines.observe(viewLifecycleOwner) {
            when (it) {
                is DataHandler.LOADING -> {
                    showProgressBar()
                }

                is DataHandler.SUCCESS -> {
                    hideProgressBar()
                    Log.d("TAG", "setObserver: ${it.data}")

                    homeViewModel.articlesList.addAll(
                        it.data?.articles?.toMutableList() ?: mutableListOf()
                    )
                    newsAdapter.addData(homeViewModel.articlesList)
                    val totalPages = kotlin.math.ceil(it.data?.totalResults?.div(QUERY_PAGE_SIZE.toDouble()) ?: 0.0)
                    isLastPage = homeViewModel.searchNewsPage == totalPages.toInt()
                    homeViewModel.searchNewsPage++
                    if (it.data?.articles?.isNotEmpty() == true) {
                        it.data.articles.forEach { article ->
                            if (article != null) {
                                article.id = md5(
                                    article.author ?: "",
                                    article.publishedAt ?: "",
                                    article.title ?: "",
                                )
                                articleViewModel.insertArticleReplace(article)
                            }
                        }
                    }
                }

                else -> {
                    hideProgressBar()
                    requireContext().toast("Running in offline mode")
                    isOffline = true
                    getPagedArticles(limit,offset)
                }
            }
        }
    }

    private fun setRecyclerView() {
        newsAdapter = NewsAdapter() { article ->

            val intent = Intent(requireActivity(), NewsDetailsActivityCompose::class.java)
            intent.putExtra("Article", article)
            requireActivity().startActivity(intent)

        }

        /*binding.rvNews.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                lp.height = height / 2
                return true
            }
        }*/
        binding.rvNews.adapter = newsAdapter
        binding.rvNews.addOnScrollListener(scrollListener)
    }

    private fun hideProgressBar() {
        binding.centerProgressBar.gone()
        binding.paginationProgressBar.gone()
        isLoading = false
    }

    private fun showProgressBar() {
        if(homeViewModel.articlesList.isEmpty()) {
            binding.centerProgressBar.visible()
        } else {
            binding.paginationProgressBar.visible()
        }
        isLoading = true
    }

    private fun getPagedArticles(limit: Int, offset: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("HomeFragment", "e: limit: $limit and offset: $offset")
            val articlesList = articleViewModel.getPagedArticles(limit, offset)
            homeViewModel.articlesList.addAll(
                articlesList?.toMutableList() ?: mutableListOf()
            )
            this@HomeFragment.offset = offset + limit
            withContext(Dispatchers.Main) {
            newsAdapter.addData(homeViewModel.articlesList)
        }
    }
    }

}