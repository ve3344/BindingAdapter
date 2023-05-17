package me.lwb.adapter.wheel

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ve3344@qq.com.
 * 滚轮选择器
 */
class RecyclerWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : RecyclerView(context, attrs) {
    val wheelModule = RecyclerWheelViewModule(this)
    override fun setAdapter(adapter: Adapter<ViewHolder>?) {
        if (adapter.isWheelAdapter()) {
            super.setAdapter(adapter)
            return
        }
        wheelModule.setAdapter(adapter)
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        require(layout is LinearLayoutManager) { "Require LinearLayoutManager" }
        super.setLayoutManager(layout)
    }
    private fun Adapter<*>?.isWheelAdapter(): Boolean {
        return this is ConcatAdapter && adapters.any { it is RecyclerWheelViewModule.OffsetAdapter }
    }

}



