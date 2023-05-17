package me.lwb.adapter.demo.ui.activity.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.demo.vm.TestData
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.basic.setupAutoNotifyModule
import me.lwb.utils.android.ext.toast
import java.util.*

@Page("增加和删除", Page.Group.BASIC)
class MutableActivity : AppCompatActivity() {
    val binding: ActivitySimpleBinding by viewBinding()

    val adapter =
        BindingAdapter(ItemSimpleBinding::inflate, TestData.stringList()) { position, item ->
            itemBinding.title.text = "[$position] $item"
            itemBinding.root.setOnClickListener {
                toast("$position")
                removeAt(position)
            }
        }

    private fun removeAt(position: Int) {
        adapter.data.removeAt(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.adapter = adapter

        adapter.setupAutoNotifyModule()


        binding.actionList.adapter = ActionAdapter(
            "Append" to Runnable { adapter.data.add("${Date()}") },
            "Prepend" to Runnable { adapter.data.add(0, "${Date()}") }
        )

    }
}