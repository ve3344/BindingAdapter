package me.lwb.adapter.demo.ui.activity.nested

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.ItemNested2DayBinding
import me.lwb.adapter.demo.databinding.ItemNested2OrderBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.NestedViewModel
import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.buildMultiTypeAdapterByType

/**
 * 嵌套布局
 */
@Page("多Type嵌套", Page.Group.NESTED)
class NestedByTypeActivity : AppCompatActivity() {
    private val binding: ActivitySimpleBinding by viewBinding()
    private val vm: NestedViewModel by viewModels()

    private val dataAdapter = buildMultiTypeAdapterByType {
        layout(ItemNested2DayBinding::inflate) { position, dayItem: NestedViewModel.OrderDay ->
            itemView.setOnClickListener {
                val adapterLocal = adapter as MultiTypeBindingAdapter<*, *>
                if (adapterLocal.data.getOrNull(position + 1) is NestedViewModel.OrderItem) {
                    adapterLocal.data.removeAt(position + 1)
                    adapter.notifyItemRemoved(position + 1)
                }
            }
            itemBinding.day.text = dayItem.day
        }
        layout(ItemNested2OrderBinding::inflate) { _, orderItem: NestedViewModel.OrderItem ->
            itemBinding.orderName.text = orderItem.orderName
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.adapter = dataAdapter

        vm.dayOrdersMerge.observe(this) {
            dataAdapter.changeDataList(it.toMutableList())
        }

        vm.loadOrdersMerge()
    }


}