package me.lwb.adapter.demo.ui.activity.loadmore

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.databinding.ActivityLoadMoreBinding
import me.lwb.adapter.demo.databinding.ItemProjectBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.demo.vm.LoadMoreViewModel
import me.lwb.adapter.ext.replaceData
import me.lwb.adapter.loadmore.LoadMoreStatus
import me.lwb.adapter.loadmore.setupLoadMoreModule

@Page("分页加载线性布局", Page.Group.LOAD_MORE)
class LoadMoreLinearActivity : AppCompatActivity() {
    val vm: LoadMoreViewModel by viewModels()
    val binding: ActivityLoadMoreBinding by viewBinding()

    //配置数据布局
    private val dataAdapter =
        BindingAdapter(ItemProjectBinding::inflate) { _, item: ProjectBean ->
            itemBinding.vm = item
            itemBinding.executePendingBindings()
        }
    private val logAdapter =
        BindingAdapter(ItemSimpleBinding::inflate) { _, item: String ->
            itemBinding.title.text = item
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.logList.adapter = logAdapter
        lifecycleScope.launch {
            vm.loadLogs.collectLatest { logAdapter.replaceData(it) }
        }

        binding.actionList.adapter = ActionAdapter("旋转屏幕" to Runnable {
            requestedOrientation = if (requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }else{
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        })
        val loadMoreModule = dataAdapter.setupLoadMoreModule()

        binding.list.adapter = LoadMoreAdapters.CommonAdapter(loadMoreModule)
        binding.list.layoutManager = LinearLayoutManager(this)

        loadMoreModule.setDataSource(lifecycleScope, vm.projects)
        binding.root.setOnRefreshListener { loadMoreModule.reload() }
        loadMoreModule.addLoadMoreStatusListener {
            if (it !is LoadMoreStatus.Loading) {
                binding.root.isRefreshing = false
            }
        }

    }
}