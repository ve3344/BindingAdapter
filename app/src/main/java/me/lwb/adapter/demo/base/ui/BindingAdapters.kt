package me.lwb.adapter.demo.base.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

/**
 * Created by ve3344@qq.com.
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("url")
    fun loadImage(view: ImageView, url: String) {
        view.load(url)
    }

}

