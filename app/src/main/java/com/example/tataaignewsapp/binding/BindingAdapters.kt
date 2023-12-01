package com.example.tataaignewsapp.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.tataaignewsapp.formatDate
import com.example.tataaignewsapp.gone
import com.example.tataaignewsapp.loadImageFromGlide
import com.example.tataaignewsapp.visible


@BindingAdapter("app:setImageFromURL")
fun setImageFromURL(imageView: ImageView, url: String?) {
    if(url.isNullOrEmpty().not()) {
        imageView.loadImageFromGlide(url)
    }
}

@BindingAdapter("app:itemVisibility")
fun itemVisibility(view: View, strValue: String?) {
    if(strValue.isNullOrEmpty()) {
        view.gone()
    } else {
        view.visible()
    }
}

@BindingAdapter("app:setDateAndTime")
fun setDateAndTime(textView: TextView, dateAndTime: String?) {
    if(dateAndTime?.isNotEmpty() == true) {
        textView.text = formatDate(
            dateAndTime,
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "dd.MM.yyyy 'at' hh:mm a"
        )
    }
}