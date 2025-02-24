package com.pro.book.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.pro.book.R

object GlideUtils {
    @JvmStatic
    fun loadUrlBanner(url: String?, imageView: ImageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.image_no_available)
            return
        }
        try {
            Glide.with(imageView.context)
                .load(url)
                .error(R.drawable.image_no_available)
                .dontAnimate()
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun loadUrl(url: String?, imageView: ImageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.image_no_available)
            return
        }
        try {
            Glide.with(imageView.context)
                .load(url)
                .error(R.drawable.image_no_available)
                .dontAnimate()
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}