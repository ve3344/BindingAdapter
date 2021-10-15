package me.lwb.adapter_demo.base.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

/**
 * Created by luowenbin on 2021/10/14.
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("url")
    fun loadImage(view: ImageView, url: String) {
        view.load(url)
    }

}

