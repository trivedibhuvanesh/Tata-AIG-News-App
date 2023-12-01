package com.example.tataaignewsapp

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date

fun getCircularLoader(context: Context): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 8f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary),
    )
    circularProgressDrawable.start()
    return circularProgressDrawable
}
fun ImageView.loadImageFromGlide(url: String?) {
    if(url!=null) {

        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.no_image_available_600_x_400)
            .placeholder(getCircularLoader(this.context))
            .into(this)
    }
}

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun formatDate(inputDate: String, inputFormat: String, outputFormat: String): String {
    var date = inputDate
    var spf = SimpleDateFormat(inputFormat)
    val newDate: Date = spf.parse(date)
    spf = SimpleDateFormat(outputFormat)
    date = spf.format(newDate)
    return date
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun md5(author: String, publishedAt: String, title: String): String {
    val input = author+publishedAt+title
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}