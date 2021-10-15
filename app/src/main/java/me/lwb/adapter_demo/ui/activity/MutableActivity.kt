package me.lwb.adapter_demo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import me.lwb.adapter_demo.base.ui.OnListChangedNotifier
import me.lwb.adapter_demo.base.ui.viewBinding
import me.lwb.adapter_demo.databinding.ActivitySimpleBinding
import me.lwb.adapter_demo.databinding.ItemPageBinding
import me.lwb.adapter_demo.databinding.ItemSimpleBinding
import me.lwb.bindingadapter.BindingAdapter
import java.util.*

class MutableActivity : AppCompatActivity() {
    val binding: ActivitySimpleBinding by viewBinding()
    val list: ObservableList<String> = ObservableArrayList()

    val adapter = BindingAdapter(ItemSimpleBinding::inflate, list) { position, item ->
        binding.title.text = "[$position] $item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        list.addOnListChangedCallback(OnListChangedNotifier(adapter))

        binding.list.adapter = adapter

        binding.add.setOnClickListener {
            list.add("${Date()}")
        }

    }
}