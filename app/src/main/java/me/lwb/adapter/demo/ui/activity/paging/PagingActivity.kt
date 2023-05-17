package me.lwb.adapter.demo.ui.activity.paging

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.base.ui.wrapSwipeRefresh
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.databinding.ActivityPagingBinding
import me.lwb.adapter.demo.databinding.ItemProjectBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.PagingViewModel
import me.lwb.adapter.paging.PagingBindingAdapter
import me.lwb.adapter.ext.configFullSpan
import me.lwb.adapter.ext.getAdapterByItemPosition

/**
 * Created by ve3344@qq.com.
 */

@Page("分页加载", Page.Group.PAGING3)
open class PagingActivity : AppCompatActivity() {
    val vm: PagingViewModel by viewModels()
    val binding: ActivityPagingBinding by viewBinding()

    //配置数据布局
    val dataAdapter =
        PagingBindingAdapter(ItemProjectBinding::inflate) { _, item: ProjectBean ->
            itemBinding.vm = item
            itemBinding.executePendingBindings()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = vm
        //添加 SwipeRefreshLayout
        binding.list.wrapSwipeRefresh(dataAdapter)


        //添加 其它布局
        val adapter = PagingAdapters.CommonAdapter(dataAdapter)
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)
        //set layoutManager
        binding.list.layoutManager = GridLayoutManager(this, 2).apply {
            configFullSpan {
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