package com.example.tataaignewsapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tataaignewsapp.viewmodel.ArticleViewModel
import com.example.tataaignewsapp.ui.adapter.NewsAdapter
import com.example.tataaignewsapp.ui.activity.NewsDetailsActivityCompose
import com.example.tataaignewsapp.R
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.databinding.FragmentBookmarksBinding
import com.example.tataaignewsapp.util.gone
import com.example.tataaignewsapp.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private lateinit var binding: FragmentBookmarksBinding
    private lateinit var newsAdapter: NewsAdapter
    private val articleViewModel: ArticleViewModel by viewModels()
    private var articlesList: MutableList<Article?> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmarks, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setupArticleSearch()

    }

    private fun getBookmarkedArticles(keyword: String) {
        binding.centerProgressBar.visible()
        lifecycleScope.launch {


            val bookmarkedArticlesList = CoroutineScope(Dispatchers.IO).async {
                articleViewModel.getBookmarkedArticlesByTitle(keyword)
            }

            withContext(Dispatchers.Main) {
                articlesList = bookmarkedArticlesList.await()?.toMutableList() ?: mutableListOf()
                newsAdapter.setData(articlesList)

                binding.centerProgressBar.gone()
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

    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<TextView>(R.id.tv_current_header).text = "Bookmarks"
        getBookmarkedArticles(articleViewModel.searchKeyword)
    }

    private fun setupArticleSearch() {
        var job: Job? = null
        binding.textInputEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    articleViewModel.searchKeyword = p0.toString()
                    val bookmarkedArticlesList = articleViewModel.getBookmarkedArticlesByTitle(p0.toString())
                        articlesList = bookmarkedArticlesList?.toMutableList() ?: mutableListOf()

                    withContext(Dispatchers.Main) {
                        newsAdapter.setData(articlesList)
                    }

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }
}