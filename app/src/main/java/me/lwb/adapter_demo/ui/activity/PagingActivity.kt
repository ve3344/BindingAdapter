package me.lwb.adapter_demo.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import me.lwb.adapter_demo.base.ui.*
import me.lwb.adapter_demo.data.bean.ProjectBean
import me.lwb.adapter_demo.databinding.ActivityPagingBinding
import me.lwb.adapter_demo.databinding.ItemProjectBinding
import me.lwb.adapter_demo.vm.PagingViewModel
import me.lwb.adapter_demo.ui.adapter.configPagingWithHeader
import me.lwb.bindinadapter.paging.PagingBindingAdapter
import me.lwb.bindingadapter.configSingleViewSpan
import me.lwb.bindingadapter.getAdapterByItemPosition

/**
 * Created by ve3344@qq.com on 2021/9/27.
 */
open class PagingActivity : AppCompatActivity() {
    val vm: PagingViewModel by viewModels()
    val binding: ActivityPagingBinding by viewBinding()

    //配置数据布局
    val dataAdapter =
         PagingBindingAdapter(ItemProjectBinding::inflate) { _, item: ProjectBean ->
            binding.vm = item
            binding.executePendingBindings()
        }

    private val TAG: String = this.javaClass.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = vm
        //添加 SwipeRefreshLayout
        binding.list.wrapSwipeRefresh(dataAdapter)


        //添加 其它布局
        val adapter = dataAdapter.configPagingWithHeader()
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)
        //set layoutManager
        binding.list.layoutManager = GridLayoutManager(this, 2).apply {
            configSingleViewSpan {
                adapter.getAdapterByItemPosition(it) !is PagingDataAdapter<*, *>
            }
        }
        //adapter2string(binding.list.adapter)

        //observe paging data
        vm.projects.observe(this) {
            dataAdapter.submitData(lifecycle, it)
        }

    }

}