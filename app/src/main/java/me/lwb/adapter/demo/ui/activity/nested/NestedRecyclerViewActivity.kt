package me.lwb.adapter.demo.ui.activity.nested

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.ItemNestedDayBinding
import me.lwb.adapter.demo.databinding.ItemNestedOrderBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.NestedViewModel
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.ext.doAfterCreateViewHolder
import me.lwb.adapter.basic.setupAutoNotifyModule

/**
 * 嵌套布局
 */
@Page("多recyclerview嵌套", Page.Group.NESTED)
class NestedRecyclerViewActivity : AppCompatActivity() {
    private val binding: ActivitySimpleBinding by viewBinding()
    private val vm: NestedViewModel by viewModels()


    private val orderViewPool = RecyclerView.RecycledViewPool()
    private fun childAdapter(orders: Array<NestedViewModel.OrderItem>) =
        BindingAdapter(ItemNestedOrderBinding::inflate, orders.toList()) { _, orderItem ->
            itemBinding.orderName.text = orderItem.orderName
        }.apply { setupAutoNotifyModule() }

    private val dataAdapter =
        BindingAdapter(ItemNestedDayBinding::inflate) { _, dayItem: NestedViewModel.OrderDay ->
            val childAdapter = childAdapter(dayItem.orders)

            itemView.setOnClickListener {
                if (childAdapter.data.isNotEmpty()) {
                    childAdapter.data.removeAt(0)
                }
            }
            itemBinding.day.text = dayItem.day
            itemBinding.orders.adapter = childAdapter
        }.doAfterCreateViewHolder { holder, _, _ ->
            holder.itemBinding.orders.setRecycledViewPool(orderViewPool)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.adapter = dataAdapter

        vm.dayOrders.observe(this){
            dataAdapter.changeDataList(it.toMutableList())
        }

        vm.loadOrders()
    }



}