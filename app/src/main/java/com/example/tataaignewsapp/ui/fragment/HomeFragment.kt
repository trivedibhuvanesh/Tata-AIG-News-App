package com.example.tataaignewsapp.ui.fragment

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
import com.example.tataaignewsapp.viewmodel.ArticleViewModel
import com.example.tataaignewsapp.api.DataHandler
import com.example.tataaignewsapp.viewmodel.HomeViewModel
import com.example.tataaignewsapp.ui.adapter.NewsAdapter
import com.example.tataaignewsapp.ui.activity.NewsDetailsActivityCompose
import com.example.tataaignewsapp.util.QUERY_PAGE_SIZE
import com.example.tataaignewsapp.R
import com.example.tataaignewsapp.databinding.FragmentHomeBinding
import com.example.tataaignewsapp.util.gone
import com.example.tataaignewsapp.util.isInternetOn
import com.example.tataaignewsapp.util.md5
import com.example.tataaignewsapp.util.toast
import com.example.tataaignewsapp.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val articleViewModel: ArticleViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    private var job: Job? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !homeViewModel.isLoading && !homeViewModel.isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && homeViewModel.isScrolling
            if (shouldPaginate) {
                if(homeViewModel.isOffline) {
                    getPagedArticles(homeViewModel.limit,homeViewModel.offset)
                } else {

                    callNewsApi()
                }
                homeViewModel.isScrolling = false
            } else {
                binding.rvNews.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                homeViewModel.isScrolling = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        if(homeViewModel.articlesList.isEmpty()) {
            callNewsApi()
        }
        setObserver()

        if(homeViewModel.isOffline){
            newsAdapter.addData(homeViewModel.articlesList)
        }
    }

    private fun callNewsApi() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            yield()
            if(isInternetOn(requireContext())) {
                homeViewModel.getTopHeadlines(
                    "in", QUERY_PAGE_SIZE
                )
            } else {
                getPagedArticles(homeViewModel.limit, homeViewModel.offset)
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

                    newsAdapter.addData(homeViewModel.articlesList)
                    val totalPages = kotlin.math.ceil(it.data?.totalResults?.div(QUERY_PAGE_SIZE.toDouble()) ?: 0.0)
                    homeViewModel.isLastPage = homeViewModel.searchNewsPage >= totalPages.toInt()
                    if (it.data?.articles?.isNotEmpty() == true) {
                        it.data.articles.forEach { article ->
                            if (article != null) {
                                article.id = md5(
                                    article.author ?: "",
                                    article.publishedAt,
                                    article.title,
                                )
                                articleViewModel.insertArticleReplace(article)
                            }
                        }
                    }
                }

                else -> {
                    hideProgressBar()
                    requireContext().toast("Running in offline mode")
                    getPagedArticles(homeViewModel.limit,homeViewModel.offset)
                }
            }
        }
    }

    private fun setRecyclerView() {

        newsAdapter = NewsAdapter { article ->

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
        homeViewModel.isLoading = false
    }

    private fun showProgressBar() {
        if(homeViewModel.articlesList.isEmpty()) {
            binding.centerProgressBar.visible()
        } else {
            binding.paginationProgressBar.visible()
        }
        homeViewModel.isLoading = true
    }

    private fun getPagedArticles(limit: Int, offset: Int) {
        showProgressBar()
        homeViewModel.isOffline = true
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(500) // Just to show that Pagination is working
            Log.d("HomeFragment", "e: limit: $limit and offset: $offset")
            val articlesList = articleViewModel.getPagedArticles(limit, offset)
            homeViewModel.articlesList.addAll(
                articlesList?.toMutableList() ?: mutableListOf()
            )
            this@HomeFragment.homeViewModel.offset = offset + limit
            withContext(Dispatchers.Main) {
                if(articlesList?.isEmpty() == true) {
                    binding.rvNews.removeOnScrollListener(scrollListener)
                }
            newsAdapter.addData(homeViewModel.articlesList)
                hideProgressBar()
        }
    }
    }

}