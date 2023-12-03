package com.example.tataaignewsapp.ui.fragment

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tataaignewsapp.api.DataHandler
import com.example.tataaignewsapp.ui.adapter.NewsAdapter
import com.example.tataaignewsapp.ui.activity.NewsDetailsActivityCompose
import com.example.tataaignewsapp.util.QUERY_PAGE_SIZE
import com.example.tataaignewsapp.R
import com.example.tataaignewsapp.viewmodel.SearchViewModel
import com.example.tataaignewsapp.databinding.FragmentSearchBinding
import com.example.tataaignewsapp.util.gone
import com.example.tataaignewsapp.util.isInternetOn
import com.example.tataaignewsapp.util.toast
import com.example.tataaignewsapp.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var newsAdapter: NewsAdapter

    private var job: Job? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !searchViewModel.isLoading && !searchViewModel.isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && searchViewModel.isScrolling
            if(shouldPaginate) {
                callSearchApi()
                searchViewModel.isScrolling = false
            } else {
                binding.rvNews.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                searchViewModel.isScrolling = true
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

                job?.cancel()
                job = MainScope().launch {
                    delay(500)
                    p0?.let {
                        if (p0.toString().isNotEmpty()) {
                            searchViewModel.searchQuery = p0.toString()

                            searchViewModel.articlesList.clear()
                            setRecyclerView()
                            searchViewModel.searchNewsPage = 1
                            callSearchApi()
                        }
                    }
                }
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
            yield()
            if(isInternetOn(requireContext())) {
                searchViewModel.searchNews(QUERY_PAGE_SIZE)
            } else {
                withContext(Dispatchers.Main) {
                    requireContext().toast("No Internet")
                }
            }
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

                    newsAdapter.addData(searchViewModel.articlesList)
                    val totalPages = kotlin.math.ceil(it.data?.totalResults?.div(QUERY_PAGE_SIZE.toDouble()) ?: 0.0)
                    searchViewModel.isLastPage = searchViewModel.searchNewsPage >= totalPages.toInt()
                }

                else -> {
                    hideProgressBar()
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
        binding.rvNews.adapter = newsAdapter
        binding.rvNews.addOnScrollListener(scrollListener)
    }

    private fun hideProgressBar() {
        binding.centerProgressBar.gone()
        binding.paginationProgressBar.gone()
        searchViewModel.isLoading = false
    }

    private fun showProgressBar() {
        if(searchViewModel.articlesList.isEmpty()) {
            binding.centerProgressBar.visible()
        } else {
            binding.paginationProgressBar.visible()
        }
        searchViewModel.isLoading = true
    }
}