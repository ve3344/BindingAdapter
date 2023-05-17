package me.lwb.adapter.demo.ui.activity.basic

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.flow.collect
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.demo.vm.DiffViewModel
import me.lwb.adapter.demo.vm.TestData
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.basic.setupDiffModule
import me.lwb.utils.android.ext.toast

@Page("增加和删除Diff", Page.Group.BASIC)
class DiffActivity : AppCompatActivity() {
    val binding: ActivitySimpleBinding by viewBinding()

    val viewModel by viewModels<DiffViewModel>()
    val adapter =
        BindingAdapter(ItemSimpleBinding::inflate, TestData.stringList()) { position, item ->
            itemBinding.title.text = "[$position] $item"
            itemBinding.root.setOnClickListener {
                toast("$position")
                viewModel.remove(item)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.adapter = adapter

        val diffModule= adapter.setupDiffModule(object: DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem==newItem
            override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem==newItem
        })
        lifecycleScope.launchWhenCreated {
            viewModel.dataFlow.collect {
                diffModule.submitList(it)
            }
        }


        binding.actionList.adapter = ActionAdapter(
            "Reload" to Runnable {
                viewModel.reload()
            },
            "Add" to Runnable {
                viewModel.add()
            },
        )

    }


}