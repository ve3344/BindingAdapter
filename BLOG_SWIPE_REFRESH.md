# 结合Paging3 快速添加下拉刷新

项目地址[binding-adapter](https://github.com/ve3344/binding-adapter)

下拉刷新直接使用SwipeRefreshLayout，看看实现效果。
直接一句代码完成。
```kotlin
binding.list.wrapSwipeRefresh(dataAdapter)
```
#### 实现原理
要结合Paging3给RecyclerView主要完成3个工作。
1. 给RecyclerView 套上SwipeRefreshLayout。
可以直接在xml中完成，但是每个RecyclerView都这么操作有点繁琐，也可通过代码更改布局。
```kotlin
/**
 * 为View包裹ViewGroup
 */
fun <T : ViewGroup> View.wrapViewGroup(block: (Context) -> T): T {
    val viewGroup = (parent as? ViewGroup)
    val index = viewGroup?.indexOfChild(this) ?: -1

    viewGroup?.removeView(this)

    val child = this
    val childLayoutParams = layoutParams

    return block(context).apply {
        viewGroup?.addView(this, index, childLayoutParams)
        addView(
            child,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}

/**
 * 为View包裹SwipeRefreshLayout
 */
fun View.wrapSwipeRefreshLayout() = wrapViewGroup { SwipeRefreshLayout(context) }

```

2. 给adapter添加刷新结束监听，隐藏加载控件。
```kotlin
adapter.addLoadStateListener { swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading }
```
3. 给SwipeRefreshLayout添加下拉刷新监听，控制adapter 重新加载。
```kotlin
swipeRefreshLayout.setOnRefreshListener {
        adapter.refresh()
    }
```

这里直接结合2、3 做一个拓展函数
```kotlin

/**
 * Created by ve3344@qq.com on 2021/9/23.
 */
/**
 * 绑定SwipeRefreshLayout和PagingDataAdapter 的刷新状态
 */
fun SwipeRefreshLayout.bindAdapter(adapter: PagingDataAdapter<*, *>): SwipeRefreshLayout {
    this.setOnRefreshListener {
        adapter.refresh()
    }
    adapter.addLoadStateListener { this.isRefreshing = it.refresh is LoadState.Loading }
    return this
}

```

然后将1、2、3在做一个拓展函数
```kotlin
/**
 * 为RecyclerView 包裹SwipeRefreshLayout 并绑定刷新
 */
fun RecyclerView.wrapSwipeRefresh(adapter: PagingDataAdapter<*, *>) {
    this.wrapSwipeRefreshLayout().bindAdapter(adapter)
}
```

分多个拓展函数是为了更灵活配置，如果以及在xml中加了SwipeRefreshLayout也可以直接使用
```kotlin
swipeRefreshLayout.bindAdapter(adapter)
```