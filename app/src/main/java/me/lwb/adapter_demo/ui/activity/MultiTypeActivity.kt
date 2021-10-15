package me.lwb.adapter_demo.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import me.lwb.adapter_demo.base.ui.viewBinding
import me.lwb.adapter_demo.ui.adapter.configPagingWithHeader
import me.lwb.adapter_demo.data.bean.ProjectBean
import me.lwb.adapter_demo.databinding.*
import me.lwb.adapter_demo.vm.PagingViewModel
import me.lwb.adapter_demo.base.ui.wrapSwipeRefresh
import me.lwb.bindingadapter.MultiTypeAdapterUtils
import me.lwb.bindinadapter.paging.asPagingAdapter

/**
 * Created by ve3344@qq.com on 2021/9/27.
 */
open class MultiTypeActivity : AppCompatActivity() {
    val vm: PagingViewModel by viewModels()
    val binding: ActivityPagingBinding by viewBinding()

    //配置数据多布局
    val adapter =
        MultiTypeAdapterUtils.createConfig<ProjectBean> {
            //配置item到布局类型的映射
            extractItemViewType { position, item ->
                position%2
            }

            layout(ItemProject2Binding::inflate) { _, item: ProjectBean ->
                binding.vm = item
                binding.executePendingBindings()
            }
            layout(ItemProjectBinding::inflate) { _, item: ProjectBean ->
                binding.vm = item
                binding.executePendingBindings()
            }

        }.asPagingAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.vm = vm

        binding.list.adapter = adapter.configPagingWithHeader()
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.wrapSwipeRefresh(adapter)
        adapter.refresh()
        vm.projects.observe(this) {
            adapter.submitData(lifecycle, it)
        }

    }

}