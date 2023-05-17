package me.lwb.adapter.demo.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by ve3344@qq.com.
 */
open class DiffViewModel : ViewModel() {
    val dataFlow = MutableStateFlow<List<String>>(TestData.stringList())
    var number = 1000

    fun add() {
        dataFlow.value = dataFlow.value+ listOf(number++.toString())
    }

    fun remove(text: String) {
        dataFlow.value = dataFlow.value.filterNot { it==text }
    }

    fun reload() {
        dataFlow.value = TestData.stringList()
    }
}