package com.example.tataaignewsapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tataaignewsapp.R
import com.example.tataaignewsapp.data.model.Article
import com.example.tataaignewsapp.databinding.ItemNewsBinding
import com.example.tataaignewsapp.util.DiffUtilId
import com.example.tataaignewsapp.util.DiffUtilTitle

class NewsAdapter(
    private val onItemClick: ((url:  Article) -> Unit)
): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private var oldArticleList: MutableList<Article?>? = mutableListOf()


    fun setData(newArticleList: MutableList<Article?>?) {
        val diffUtil = DiffUtilId(oldArticleList, newArticleList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldArticleList = newArticleList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addData(newArticleList: MutableList<Article?>?) {
        val diffUtil = DiffUtilTitle(oldArticleList, newArticleList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldArticleList = newArticleList
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemNewsBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_news, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return oldArticleList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = oldArticleList?.get(position)
            binding.newsItem =  item

            binding.articleCard.setOnClickListener {
                if(item != null) {
                    onItemClick.invoke(item)
                }
            }
        }
    }

    class ViewHolder(val binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root)


}