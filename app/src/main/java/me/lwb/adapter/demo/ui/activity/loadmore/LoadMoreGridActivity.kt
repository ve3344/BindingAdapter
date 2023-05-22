package me.lwb.adapter.demo.ui.activity.loadmore

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.databinding.ActivityLoadMoreBinding
import me.lwb.adapter.demo.databinding.ItemProjectBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.LoadMoreViewModel
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.ext.configFullSpan
import me.lwb.adapter.ext.getAdapterByItemPosition
import me.lwb.adapter.loadmore.LoadMoreStatus
import me.lwb.adapter.loadmore.setupLoadMoreModule

@Page("分页加载网格布局", Page.Group.LOAD_MORE)
class LoadMoreGridActivity : AppCompatActivity() {
    val vm: LoadMoreViewModel by viewModels()
    val binding: ActivityLoadMoreBinding by viewBinding()

    //配置数据布局
    private val dataAdapter =
        BindingAdapter(ItemProjectBinding::inflate) { _, item: ProjectBean ->
            itemBinding.vm = item
            itemBinding.executePendingBindings()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loadMoreModule = dataAdapter.setupLoadMoreModule()

        val concatAdapter = LoadMoreAdapters.CommonAdapter(loadMoreModule)
        binding.list.adapter = concatAdapter

        binding.list.layoutManager = GridLayoutManager(this, 3).apply {
//            configFullSpan { concatAdapter.findLocalPositionAt(dataAdapter,it)==RecyclerView.NO_POSITION }
            configFullSpan { concatAdapter.getAdapterByItemPosition(it) != dataAdapter }
        }

        loadMoreModule.setDataSource(lifecycleScope, vm.projects)
        binding.root.setOnRefreshListener { loadMoreModule.reload() }
        loadMoreModule.addLoadMoreStatusListener {
            if (it !is LoadMoreStatus.Loading) {
                binding.root.isRefreshing = false
            }
        }

    }
}