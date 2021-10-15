package me.lwb.bindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BindingViewHolder<T : ViewBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root) {

    private var cacheParams: ViewGroup.LayoutParams? = binding.root.layoutParams

    /**
     * 数据item的gone控制，设置gone将不会占据大小
     */
    var isGone: Boolean
        set(value) {
            if (binding.root.layoutParams !== GONE_PARAMS) {
                cacheParams = binding.root.layoutParams
            }
            binding.root.layoutParams = if (value) GONE_PARAMS else cacheParams
            binding.root.visibility = if (value) View.GONE else View.VISIBLE
        }
        get() = binding.root.visibility == View.GONE

    companion object {
        private val GONE_PARAMS = ViewGroup.LayoutParams(0, 0)

    }

}

