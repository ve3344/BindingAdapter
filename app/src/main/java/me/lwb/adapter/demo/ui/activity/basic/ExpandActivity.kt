package me.lwb.adapter.demo.ui.activity.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityExpandBinding
import me.lwb.adapter.demo.databinding.HeaderExpandBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.TestData
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.ext.plus
@Page("显示和隐藏", Page.Group.BASIC)
class ExpandActivity : AppCompatActivity() {
    private val binding: ActivityExpandBinding by viewBinding()
    private val dataAdapter =
        BindingAdapter(ItemSimpleBinding::inflate, TestData.stringList()) { _, item ->
            itemBinding.title.text = item
        }
    private val titleAdapter = SingleViewBindingAdapter(HeaderExpandBinding::inflate) {
        itemBinding.expand.setOnCheckedChangeListener { _, isChecked ->
            dataAdapter.isVisible = isChecked
            //通过isVisible切换展开
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.adapter = titleAdapter + dataAdapter


    }
}