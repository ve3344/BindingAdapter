package me.lwb.adapter.demo.ui.activity.paging

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.base.ui.wrapSwipeRefresh
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.databinding.ActivityPagingBinding
import me.lwb.adapter.demo.databinding.ItemProject2Binding
import me.lwb.adapter.demo.databinding.ItemProjectBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.PagingViewModel
import me.lwb.adapter.ext.createMultiTypeConfigByIndex
import me.lwb.bindinadapter.paging.asPagingAdapter

/**
 * Created by ve3344@qq.com.
 */
@Page("多布局", Page.Group.PAGING3)
open class MultiTypeActivity : AppCompatActivity() {
    val vm: PagingViewModel by viewModels()
    val binding: ActivityPagingBinding by viewBinding()

    //配置数据多布局
    val adapter =
        createMultiTypeConfigByIndex {
            val type1 = layout(ItemProject2Binding::inflate) { _, item: ProjectBean ->
                itemBinding.vm = item
                itemBinding.executePendingBindings()
            }
            val type2 = layout(ItemProjectBinding::inflate) { _, item: ProjectBean ->
                itemBinding.vm = item
                itemBinding.executePendingBindings()
            }
            //配置item到布局类型的映射
            extractItemViewType { position, _ ->
                if (position % 2 == 0) type1 else type2
            }

        }.asPagingAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.vm = vm

        binding.list.adapter = PagingAdapters.CommonAdapter(adapter)
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.wrapSwipeRefresh(adapter)
        adapter.refresh()
        vm.projects.observe(this) {
            adapter.submitData(lifecycle, it)
        }

    }

}