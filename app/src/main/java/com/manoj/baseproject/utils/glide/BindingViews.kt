package com.manoj.baseproject.utils.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingViews {
    @BindingAdapter(value = ["setImageUrl", "placeHolder"], requireAll = false)
    @JvmStatic
    fun setImageUrl(
        imageView: ImageView,
        url: String?,
        placeHolder: Drawable? = null,
    ) {
        when {
            url.isNullOrEmpty() && placeHolder == null -> return
            url.isNullOrEmpty() && placeHolder != null -> Glide.with(imageView).load(placeHolder).circleCrop()
                .into(imageView)
            !url.isNullOrEmpty() && placeHolder == null -> Glide.with(imageView).load(url).circleCrop()
                .into(imageView)
            !url.isNullOrEmpty() && placeHolder != null -> Glide.with(imageView).load(url).circleCrop()
                .placeholder(placeHolder).error(placeHolder).into(imageView)
        }
    }
}