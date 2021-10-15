package me.lwb.adapter_demo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import me.lwb.adapter_demo.base.ui.viewBinding
import me.lwb.adapter_demo.databinding.*
import me.lwb.bindingadapter.BindingAdapter
import me.lwb.bindingadapter.SingleViewBindingAdapter
import me.lwb.bindingadapter.plus

class ExpandActivity : AppCompatActivity() {
    private val binding: ActivityExpandBinding by viewBinding()
    private val testDataAdapter =
        BindingAdapter(ItemTestBinding::inflate, listOf("a", "b", "c")) { position, item ->
            binding.tips.text = item
        }
    private val expandTitleAdapter = SingleViewBindingAdapter(HeaderExpandBinding::inflate) {
        binding.expand.setOnCheckedChangeListener { buttonView, isChecked ->
            testDataAdapter.isVisible = isChecked
            //通过isVisible切换展开
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.adapter = expandTitleAdapter + testDataAdapter


    }
}