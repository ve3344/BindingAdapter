package me.lwb.adapter.loadmore

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.lwb.adapter.MultiTypeBindingAdapter
import me.lwb.adapter.ext.appendData
import me.lwb.adapter.ext.doAfterBindViewHolder
import me.lwb.adapter.ext.postAvoidComputingLayout
import me.lwb.adapter.ext.replaceData

/**
 * Created by ve3344@qq.com.
 * 分页加载的Adapter帮助类
 */
class LoadMoreAdapterModule<I : Any, V : ViewBinding>(val adapter: MultiTypeBindingAdapter<I, V>, ) {
    /**
     * 当前绑定的数据源
     */
    private var attachedData: LoadMoreDataSource<I>? = null
    /**
     * 控制加载模块
     */
    private val uiReceiver get() = attachedData?.uiReceiver
    /**
     * 加载状态监听
     */
    private val statusListeners = mutableListOf<(LoadMoreStatus) -> Unit>()

    /**
     * 保存job，切换data时取消订阅
     */
    private var dataCollectJob: Job? = null
    /**
     * 加载状态
     */
    val loadStatus: LoadMoreStatus get() = attachedData?.statusFlow?.value ?: LoadMoreStatus.Idle



    init {
        //触发加载下一页
        adapter.doAfterBindViewHolder { holder, position, payloads ->
            adapter.recyclerView?.postAvoidComputingLayout {
                uiReceiver?.onItemAccess(position)
            }
        }
    }

    /**
     * 添加加载状态监听
     */
    fun addLoadMoreStatusListener(function: (LoadMoreStatus) -> Unit) {
        statusListeners += function
    }

    /**
     * 添加加载状态监听
     */
    fun observeLoadMoreStatus(listener: (value: LoadMoreStatus) -> Unit) {
        addLoadMoreStatusListener(listener)
        listener.invoke(loadStatus)
    }

    /**
     * 设置数据
     * @param lifecycleOwner 绑定生命周期
     * @param data 分页数据
     * @param initReload 是否需要加载初始页，默认打开
     */
    fun setDataSource(
        lifecycleOwner: LifecycleOwner,
        data: LoadMoreDataSource<I>,
    ) = setDataSource(lifecycleOwner.lifecycleScope, data)


    /**
     * 设置数据
     * @param scope 绑定协程
     * @param data 分页数据
     * @param initReload 是否需要加载初始页，默认打开
     */
    fun setDataSource(
        scope: CoroutineScope,
        data: LoadMoreDataSource<I>
    ) {
        if (this.attachedData === data) {
            return
        }
        this.attachedData = data
        dataCollectJob?.cancel()
        dataCollectJob = scope.launch {
            launch {
                data.statusFlow.collectLatest { status ->
                    statusListeners.forEach { it(status) }
                }
            }
            launch {
                data.dataFlow.collect {
                    when (it) {
                        is DataChangeEvent.Append -> adapter.appendData(it.data)
                        is DataChangeEvent.Replace -> adapter.replaceData(it.data)
                    }
                }
            }

        }
    }

    /**
     * 重新加载
     */
    fun reload() = uiReceiver?.reload()

    /**
     * 重试加载
     */
    fun retry() = uiReceiver?.retry()

}

fun <I : Any, V : ViewBinding> MultiTypeBindingAdapter<I, V>.setupLoadMoreModule() =
    LoadMoreAdapterModule(this)