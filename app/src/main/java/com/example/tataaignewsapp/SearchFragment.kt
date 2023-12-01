package com.example.tataaignewsapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tataaignewsapp.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var newsAdapter: NewsAdapter

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    var job: Job? = null

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
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                callSearchApi()
                isScrolling = false
            } else {
                binding.rvNews.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        setRecyclerView()
        setupArticleSearch()
        setObserver()
        return binding.root
    }

    private fun setupArticleSearch() {
        binding.textInputEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchViewModel.searchQuery = p0.toString()
                callSearchApi()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<TextView>(R.id.tv_current_header).text = "Search News"

    }

    private fun callSearchApi() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            searchViewModel.searchNews(QUERY_PAGE_SIZE)
        }
    }

    private fun setObserver() {
        searchViewModel.topHeadlines.observe(viewLifecycleOwner) {
            when(it) {
                is DataHandler.LOADING -> {
                    showProgressBar()
                }

                is DataHandler.SUCCESS -> {
                    hideProgressBar()
                    Log.d("TAG", "setObserver: ${it.data}")

                    searchViewModel.articlesList.addAll(it.data?.articles?.toMutableList() ?: mutableListOf())
                    newsAdapter.addData(searchViewModel.articlesList)
                    val totalPages = (it.data?.totalResults?.div(QUERY_PAGE_SIZE))?.plus(2)
                    isLastPage = searchViewModel.searchNewsPage == totalPages
                    searchViewModel.searchNewsPage++
                }

                else -> {
                    hideProgressBar()
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
        binding.rvNews.adapter = newsAdapter
        binding.rvNews.addOnScrollListener(scrollListener)
    }

    private fun hideProgressBar() {
        binding.centerProgressBar.gone()
        binding.paginationProgressBar.gone()
        isLoading = false
    }

    private fun showProgressBar() {
        if(searchViewModel.articlesList.isEmpty()) {
            binding.centerProgressBar.visible()
        } else {
            binding.paginationProgressBar.visible()
        }
        isLoading = true
    }
}