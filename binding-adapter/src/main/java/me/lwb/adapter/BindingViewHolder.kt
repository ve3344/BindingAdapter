package me.lwb.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BindingViewHolder<T : ViewBinding>(val adapter: RecyclerView.Adapter<*>, val itemBinding: T) :
    RecyclerView.ViewHolder(itemBinding.root) {

    @Deprecated("使用itemBinding来避免和activity中的binging产生歧义", ReplaceWith("itemBinding"))
    val binding: T
        get() = itemBinding

    private var cacheParams: ViewGroup.LayoutParams? = itemBinding.root.layoutParams

    /**
     * 数据item的gone控制，设置gone将不会占据大小
     */
    var isGone: Boolean
        set(value) {
            if (itemBinding.root.layoutParams !== GONE_PARAMS) {
                cacheParams = itemBinding.root.layoutParams
            }
            itemBinding.root.layoutParams = if (value) GONE_PARAMS else cacheParams
            itemBinding.root.visibility = if (value) View.GONE else View.VISIBLE
        }
        get() = itemBinding.root.visibility == View.GONE


    private var cacheTag: Pair<Int, Any?>? = null
    fun getTag(@IdRes id: Int): Any? {
        cacheTag?.let {
            if (it.first == id) {
                return it.second
            }
        }
        return itemView.getTag(id).also { cacheTag = Pair(id, it) }
    }

    fun setTag(@IdRes id: Int, value: Any?) {
        itemView.setTag(id, value)
        cacheTag = null
    }

    companion object {
        private val GONE_PARAMS = ViewGroup.LayoutParams(0, 0)

    }

}

