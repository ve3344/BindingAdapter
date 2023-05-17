package me.lwb.adapter.demo.vm

import android.app.Application
import androidx.lifecycle.*

/**
 * Created by ve3344@qq.com.
 */
open class NestedViewModel(application: Application) :
    AndroidViewModel(application) {

    val dayOrders: MutableLiveData<List<OrderDay>> = MutableLiveData()
    val dayOrdersMerge: MutableLiveData<List<Any>> = MutableLiveData()

    fun loadOrders() {
        dayOrders.value = generateDayOrderData().toList()
    }

    fun loadOrdersMerge() {
        val orderData = generateDayOrderData()
        dayOrdersMerge.value = sequence {
            for (orderDay in orderData) {
                this.yield(orderDay)
                this.yieldAll(orderDay.orders.toList())
            }
        }.toList()
    }

    private fun generateDayOrderData(): Array<OrderDay> {
        return Array(10) {
            OrderDay("Day $it", Array(2 + it) {
                OrderItem("order_$it")
            })
        }
    }

    data class OrderDay(val day: String, val orders: Array<OrderItem>)
    data class OrderItem(val orderName: String)
}