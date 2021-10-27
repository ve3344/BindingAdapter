# ConcatAdapter和GridLayoutManager同时使用的问题

项目地址[binding-adapter](https://github.com/ve3344/binding-adapter)

ConcatAdapter能按一定顺序拼接多个Adapter生成一个新的Adapter。对添加Header，Footer，加载状态等有很大帮助，在实际使用中我们的Header、Footer往往是单独占据一行的，然而，在结合GridLayoutManager使用时容易出现只占据一部分的情况。

#### 通过type方案

在ConcatAdapter出现前，一般都是通过getItemViewType根据Header、Footer位置返回不同的type，然后创建不同的ViewHolder来实现添加Header布局的。

而ConcatAdapter的出现方便了添加Header，Footer。但是却使得结合GridLayoutManager使用更麻烦了。

#### 使用SpanSizeLookup方案
使用binding-adapter的目的就是减少Adapter的重写，因此可通过重新设置GridLayoutManager的SpanSizeLookup来实现对Header的排列。

先看看使用效果：
对SpanSizeLookup重新设置，对positon不是PagingDataAdapter<*, *>都设置为占据一行。
```kotlin
binding.list.layoutManager = GridLayoutManager(this, 2).apply {
    configSingleViewSpan {
        //当子adapter 是PagingDataAdapter 时按GirdLayout排列
        //其它adapter 按单行排列
        adapter.getAdapterByItemPosition(it) !is PagingDataAdapter<*, *>
    }
}
```

#### SpanSizeLookup方案实现原理
SpanSize：跨度，跨度是指某个item占据多少列。
SpanCount：在新建GridLayoutManager时指定，可理解为将1行分为SpanCount多少列（多少个跨度）
SpanSizeLookup：对item占据跨度的配置方案,它允许对每个item进行不同的跨度设置。
默认每个item占据跨度为1，而一行的跨度数量为SpanCount，所以不设置的时候，每行显示item的数量刚好等于SpanCount。


对SpanSizeLookup重新设置之后，我们可以指定某些item占用跨度变大(当然是不能超过SpanCount的，否则永远也放不下了。)

我们以SpanCount=2为例，
某些item跨度变成2之后，就意味着它必须要单独一行才放得下了，所以如果想要某个item单独占据一行，可以将它的跨度设置为SpanCount。

我们添加一个拓展函数完成这个操作。

```kotlin
fun GridLayoutManager.configSingleViewSpan(range: (position: Int) -> Boolean) {
    val oldSpanSizeLookup = spanSizeLookup
    val oldSpanCount = spanCount

    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (range(position)) oldSpanCount else oldSpanSizeLookup.getSpanSize(position)
        }
    }
}

```
然后下一个问题，它的参数只有position，我们怎么知道这个position的item就是我们的header呢，在单个Adapter中，我们是可以通过getViewtype，来完成的。而使用ConcatAdapter，就比较麻烦了，因为有多个Adapter，每个Adapter都有getViewtype，虽然ConcatAdapter提供了isolateViewTypes配置来共享viewtype，但是还是比较复杂的，毕竟还是需要定义各个type。

这个拿到的position其实是全局的position，也就是它的范围包括了所有adapter中的item。
而每个adapter都有itemCount属性，通过这两个不就能定位到Header和Footer在哪个Adapter了吗。

我们再添加一个拓展函数完成上述操作。
```kotlin
fun ConcatAdapter.getAdapterByItemPosition(position: Int): RecyclerView.Adapter<out RecyclerView.ViewHolder>? {
    var pos = position
    val adapters = adapters
    for (adapter in adapters) {
        when {
            pos >= adapter.itemCount -> {
                pos -= adapter.itemCount
            }
            pos < 0 -> return null
            else -> return adapter
        }
    }
    return null
}
```

于是我们便能通过position获取到当前item所属的Adapter，再判断这个Adapter是不是我们需要占据一行的Adapter就行了。
