package me.lwb.adapter.basic

import me.lwb.adapter.MultiTypeBindingAdapter

class AutoNotifyModule<T : Any>(val adapter: MultiTypeBindingAdapter<T, *>) {
    init {
        attach()

    }

    private fun attach() {
        if (adapter.data is ObservableListWrapper) {
            return
        }
        val listImpl = ObservableListWrapper(adapter.data, adapter)
        adapter.changeDataList(listImpl)

    }
}

/**
 * setupXxx 会返回Adapter本身,installXxx 会返回拓展模块
 * 安装自动通知更新模块
 */
fun <T : Any, A : MultiTypeBindingAdapter<T, *>> A.setupAutoNotifyModule(): AutoNotifyModule<T> {

    return AutoNotifyModule(this);
}
