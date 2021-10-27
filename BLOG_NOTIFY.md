# 使用ObservableList代替自动adapter.notifyXXXX刷新列表
项目地址[binding-adapter](https://github.com/ve3344/binding-adapter)

多数使用使用列表都需要动态刷新，
为了避免手动计算并调用adapter.notifyXXXX()，
可直接利用DataBinding的ObservableList。

1.将参数list使用ObservableList代替
```kotlin
val list: ObservableList<String> = ObservableArrayList()

val adapter = BindingAdapter(ItemSimpleBinding::inflate, list) { position, item ->
    binding.title.text = "[$position] $item"
}
```

2.实现通用的OnListChangedNotifier,完成变更通知adapter。

为了保持库的轻量，没有包含在库中，需要可以自行从Demo中拷贝。

```kotlin
class OnListChangedNotifier<T >(
    val adapter: RecyclerView.Adapter<*>,
) : OnListChangedCallback<ObservableList<T>>() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onChanged(sender: ObservableList<T>) {
        adapter.notifyDataSetChanged()
    }

    override fun onItemRangeChanged(
        sender: ObservableList<T>,
        positionStart: Int,
        itemCount: Int
    ) {
        adapter.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onItemRangeInserted(
        sender: ObservableList<T>,
        positionStart: Int,
        itemCount: Int
    ) {
        adapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onItemRangeMoved(
        sender: ObservableList<T>,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        for (i in 0 until itemCount) {
            adapter.notifyItemMoved(fromPosition + i, toPosition + i)
        }
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<T>,
        positionStart: Int,
        itemCount: Int
    ) {
        adapter.notifyItemRangeRemoved(positionStart, itemCount)
    }


}
```
3.绑定list和adapter
```kotlin
list.addOnListChangedCallback(OnListChangedNotifier(adapter))
```