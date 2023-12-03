package com.example.tataaignewsapp.util

import androidx.recyclerview.widget.DiffUtil
import com.example.tataaignewsapp.data.model.Article

class DiffUtilTitle(
    private val oldList: List<Article?>?,
    private val newList: List<Article?>?
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.title == newList?.get(newItemPosition)?.title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.title == newList?.get(newItemPosition)?.title
                && oldList?.get(oldItemPosition)?.description == newList?.get(newItemPosition)?.description
                && oldList?.get(oldItemPosition)?.author == newList?.get(newItemPosition)?.author
                && oldList?.get(oldItemPosition)?.publishedAt == newList?.get(newItemPosition)?.publishedAt
    }
}