package me.lwb.adapter.demo.ui.activity.loadmore

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.databinding.ActivityLoadMoreBinding
import me.lwb.adapter.demo.databinding.ItemProjectBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.LoadMoreViewModel
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.setFullSpan
import me.lwb.adapter.loadmore.LoadMoreStatus
import me.lwb.adapter.loadmore.setupLoadMoreModule

@Page("分页加载瀑布流布局", Page.Group.LOAD_MORE)
class LoadMoreStaggeredGridActivity : AppCompatActivity() {
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

        //或者自己配置
//        binding.list.adapter = LoadMoreAdapters.run {
//            ImageHeaderAdapter().setFullSpan() +
//                    ProjectHeaderAdapter().setFullSpan() +
//                    loadMoreModule.adapter +
//                    ProjectLoadMoreState(loadMoreModule).setFullSpan() +
//                    ProjectEmptyAdapter(loadMoreModule).setFullSpan() +
//                    ProjectFooterAdapter().setFullSpan()
//        }
        binding.list.adapter = LoadMoreAdapters.CommonAdapter(loadMoreModule).apply {
            adapters.filterIsInstance<MultiTypeBindingAdapter<Any, ViewBinding>>()
                .filterNot { it == dataAdapter }
                .forEach { it.setFullSpan() }
        }

        binding.list.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)

        loadMoreModule.setDataSource(lifecycleScope, vm.projects)
        binding.root.setOnRefreshListener { loadMoreModule.reload() }
        loadMoreModule.addLoadMoreStatusListener {
            if (it !is LoadMoreStatus.Loading) {
                binding.root.isRefreshing = false
            }
        }

    }
}