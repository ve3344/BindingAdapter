# 使用文档

## 创建Adapter

创建好item布局item_page.xml后，会自动生成ItemPageBinding文件

使用BindingAdapter构造方法创建Adapter，在绑定器中使用itemBinding来访问和更新Item内容

```kotlin
//in XxxActivity.ky
val adapter = BindingAdapter<ItemBean, ItemBinding>(ItemBinding::inflate) { position, item ->
    //在绑定器内，配置对ui元素和bean属性之间的绑定，设置点击事件等。
    itemBinding.title.text = item.title
    itemBinding.title.setOnClickListener {

    }
}

```

使用带有payloads的方式，只需增加payloads参数，编译器会自动切换到对应重载方法。

```kotlin
val adapter =
    BindingAdapter<ItemBean, ItemBinding>(ItemBinding::inflate) { position, item, payloads ->
    }
```

在构造时指定初始数据

```kotlin
val adapter = BindingAdapter(ItemBinding::inflate, listOf("xx")) { position, item, payloads ->

}
```

注：如果使用DataBinding 记得在绑定器内调用executePendingBindings()方法

## 创建多类型Adapter

提供了3种方式创建多类型Adapter

### 1. 通过 Class 匹配布局类型

适合于多个布局对应的Item类型是不同的情况。

- buildMultiTypeAdapterByType 通过 Class 构建多类型Adapter

```kotlin
sealed class DataType(val text: String) {
    class TitleData(text: String) : DataType(text)
    class NormalData(text: String) : DataType(text)
}
//1.通过Class类型决定ItemType
val adapter =
    buildMultiTypeAdapterByType {
        //配置item到布局类型的映射,以下2种效果是一样的，只是指定泛型方式不同
        layout(ItemSimpleTitleBinding::inflate) { _, item: DataType.TitleData ->
            itemBinding.title.text = item.text
        }
        layout<DataType.NormalData, ItemSimpleBinding>(ItemSimpleBinding::inflate) { _, item ->
            itemBinding.title.text = item.text
        }
    }

```

### 2. 自定义匹配类型

当无法按Class来区分布局类型的时候可以使用该方式

- buildMultiTypeAdapterByIndex 自定义匹配类型构建多类型Adapter

```kotlin
//2.自定义ItemType
val adapter = buildMultiTypeAdapterByIndex<DataType> {
    val type0 = layout(ItemSimpleTitleBinding::inflate) { _, item: DataType.TitleData ->
        itemBinding.title.text = item.text
    }
    val type1 = layout(ItemSimpleBinding::inflate) { _, item: DataType.NormalData ->
        itemBinding.title.text = item.text
    }
    extractItemViewType { position, item -> if (position % 10 == 0) type0 else type1 }

}
```

### 3. 自定义匹配类型(原生方式)

类似于原生的方式，需要自己维护id，不推荐

- buildMultiTypeAdapterByMap 自定义匹配类型构建多类型Adapter

```kotlin
//3.自定义ItemType
val adapter = buildMultiTypeAdapterByMap<DataType> {
    layout(0, ItemSimpleTitleBinding::inflate) { _, item: DataType.TitleData ->
        itemBinding.title.text = item.text
    }
    layout(1, ItemSimpleBinding::inflate) { _, item: DataType.NormalData ->
        itemBinding.title.text = item.text
    }
    extractItemViewType { _, item -> if (item is DataType.TitleData) 0 else 1 }
}

```

## Header和Footer

该库没有包含Header/Footer的任何实现，而是使用了RecyclerView 库中自带的ConcatAdapter来实现。

[ConcatAdapter](https://developer.android.google.cn/reference/androidx/recyclerview/widget/ConcatAdapter?hl=en)

得益于ConcatAdapter的灵活性，我们可以直接使用`+`拼接多个Adapter 来实现Header/Footer的效果，拼接顺序即Header、内容、Footer的相对位置。

SingleViewBindingAdapter 是BindingAdapter的一个子类，它只有一个元素，可以很方便生成 Header/Footer 的Adapter

- `+` 依次连接多个Adapter
- adapter.copy()
  可以拷贝一个Adapter，相当于拷贝了其onCreateViewHolder和onBindViewHolder,并使用其数据作为初始数据，后续的数据变更是相互独立的，且状态不共享。

```kotlin
val header = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)
val footer = SingleViewBindingAdapter(FooterSimpleBinding::inflate)

binding.list.adapter = header + adapter + footer

binding.list.adapter = header + adapter + header.copy() + adapter.copy() + footer //也可以任意拼接

```

- singleViewBindingAdapter.update() 更新布局状态

```kotlin
header.update {
    itemBinding.tips.text = select
}
```

## 显示和隐藏的控制

### Adapter 显示控制

由于可以通过ConcatAdapter 来实现多个Adapter的连接，那么对其中一些Adapter的元素进行显示和隐藏切换就十分有用，也是后续许多拓展模块的基础。

- adapter.isVisible 控制Adapter显示和隐藏

```kotlin
adapter.isVisible = false //隐藏
```

### Item 显示控制

由于使用itemView.visibility = View.GONE 控制item隐藏仍然占用布局空间，因此提供了isGone拓展属性来替代需要隐藏item的情况

- itemBinding.isGone 控制Item显示和隐藏

```kotlin
val adapter = BindingAdapter<ItemBean, ItemBinding>(ItemBinding::inflate) { position, item ->
    //itemBinding.itemView.visibility = View.GONE 此方式仍然占用空间
    itemBinding.isGone = item.title.isEmpty()
}

```

## 数据访问和变更

大约有3种变更方式

### 1. 原生方式

操作进行数据修改，然后调用adapter.notifyXxxx()

- adapter.data 获取当前数据List

```kotlin
adapter.data.addAll(data)

adapter.notifyDataSetChanged()
```

### 2. 常用拓展方法(建议)：

内置了一些常用的拓展方案，满足大多数情况。如需要更多也可以自定义

- adapter.appendData() 末尾追加数据

- adapter.replaceData() 替换数据

```kotlin
adapter.appendData(data)//追加数据 

adapter.replaceData(data)//替换数据
```

### 3. 替换列表实现类

- adapter.changeDataList() 替换Adapter内部List实现，实现特殊功能时使用

```kotlin
adapter.changeDataList(LinkedList())
```

通过替换列表实现类，我们可以拓展一些模块：

#### InfiniteDataModule 无限数据模块

- InfiniteListWrapper 将List数据重复，模拟无限数据

- adapter.setupInfiniteDataModule() 设置无限数据模块，一般Banner无限滚动中使用

```kotlin
adapter.setupInfiniteDataModule() //数据变成无限循环的
```

#### AutoNotifyModule 自动刷新模块

- ObservableListWrapper 将List变更通知到Adapter

- adapter.setupAutoNotifyModule() 设置自动刷新模块，自动将List的变更同步到Adapter，无需notify

```kotlin
adapter.setupAutoNotifyModule()

adapter.data.add(data)
adapter.data.add(0, data)
```

#### DiffModule 差量更新模块

- LazyListWrapper 动态的将List的代理到不同的实现

- adapter.setupDiffModule() 替换实现类为 AsyncListDiffer内部的List 来使用Differ模块，避免继承ListAdapter 或者 修改Adapter类

- diffModule.submitList() 提供新数据，差量更新模块会计算旧数据和新数据的变更，并应用到Adapter上

```kotlin
val diffModule = adapter.setupDiffModule(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
})
lifecycleScope.launchWhenCreated {
    viewModel.dataFlow.collect {
        diffModule.submitList(it)
    }
}

```

## 动态加载模块(无侵入)

本模块参考了Paging3库中的源码，进行了一些简化，主要有以下特点：

- 相比于Paging, 无需继承PagingDataAdapter，完全解耦，无需更改Adapter/RecyclerView，简化了加载状态

-

相比于大多数分页监听，[BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) [BRV](https://github.com/liangjingkanji/BRV)
不同，本模块将数据和ui分离，支持MVVM/MVP/MVC。

- 分页数据纯Kotlin实现，不依赖ViewModel/LiveData，分页Ui逻辑单独实现，切换到Compose 时无需更改数据

### LoadMoreData 数据加载

LoadMoreData类似于Pager，提供数据加载和存储功能。

使用时建议根据项目分页逻辑封装一下，大多数是pageIndex,pageSize 的模式，可按照以下封装：

- PageProgress 默认分页进度，pageIndex,pageSize 的模式

```kotlin
fun <T> ViewModel.PageLoadMoreData(fetcher: LoadMoreDataFetcher<T, PageProgress>) =
    LoadMoreData(viewModelScope, PageProgress(), fetcher)
```

在ViewModel中定义加载：

- loadMoreData.preloadCount 设置预加载数量

- loadMoreData.reload() 重新加载数据

- loadMoreData.source 数据源

```kotlin
val projects = PageLoadMoreData<ProjectBean> {
    ProjectRepository.getProjects(it.pageIndex, it.pageSize).data.datas
}
fun reload() {
    projects.reload()
}
```

在非MVVM架构中，只需把CoroutineScope使用LifecycleScope即可

### LoadMoreAdapterModule 数据展示

主要实现了数据的显示逻辑和检测数据需要加载更多的逻辑

- adapter.setupLoadMoreModule() 拓展方法添加分页模块

- loadMoreModule.setDataSource() 设置数据源

- loadMoreModule.reload() 重新加载

- loadMoreModule.retry() 错误重试

- loadMoreModule.loadStatus 获取当前加载状态

- loadMoreModule.addLoadMoreStatusListener 监听加载状态

```kotlin
val loadMoreModule = dataAdapter.setupLoadMoreModule()
loadMoreModule.setDataSource(lifecycleScope, vm.projects.source)
loadMoreModule.reload()//启动加载第一页
```

### 加载状态布局

加载状态有4种：

- LoadMoreStatus.Idle 空闲

- LoadMoreStatus.NoMore 无更多数据

- LoadMoreStatus.Fail 加载失败

- LoadMoreStatus.Loading 加载中

加载状态布局可以用一个SingleViewBindingAdapter 来完成，然后监听状态变更刷新内容，本库也提供了拓展方法来简化创建

- loadMoreModule.createLoadMoreStatusAdapter() 创建一个Adapter，同时根据LoadMoreStatus去更新Footer内容 。

```kotlin
module.createLoadMoreStatusAdapter(FooterProjectBinding::inflate) {
    when (val status = module.loadStatus) {
        is LoadMoreStatus.Fail -> {
            isGone = false

            //加载失败，显示加载失败、
            itemBinding.root.showChildOnly { it == itemBinding.loadError }
            itemBinding.loadError.setOnClickListener {
                //点击重试
                module.retry()
                itemBinding.root.showChildOnly { it == itemBinding.progressBar }
                //手动显示加载中
            }
        }
        LoadMoreStatus.Idle -> {
            //空闲，隐藏所有view
            isGone = true
        }
        is LoadMoreStatus.Loading -> {
            isGone = false
            //加载中，显示加载中
            itemBinding.root.showChildOnly { it == itemBinding.progressBar }
        }
        is LoadMoreStatus.NoMore -> {
            if (status.isReload) {
                isGone = true
                //第一页就没有数据，我们有空布局了，所以不需要显示[没有更多数据了]。
                return@createLoadStateAdapter
            }
            isGone = false

            //没有更多数据了，显示没有更多数据
            itemBinding.root.showChildOnly { it == itemBinding.reachEnd }
        }
    }
}

```

### 空布局

空布局本质上也是createLoadStateAdapter去实现的，当数据为空时，且加载状态为空闲时显示此布局

- loadMoreModule.createEmptyStateAdapter() 可以快速创建一个空布局。

需要注意，空布局和加载布局同时使用时，加载布局需要特殊处理一下判断是第一页就不需要显示了，以免 "空布局" 和 "没有更多数据了" 同时出现

```kotlin
loadMoreAdapterModule.createEmptyStateAdapter(LayoutEmptyBinding::inflate) {}
```

### 常用布局组合

加载状态布局和空布局一般在项目中一般是统一的，可以封装成项目通用Adapter

```kotlin
fun CommonAdapter(loadMoreAdapterModule: LoadMoreAdapterModule<*, *>) =
    ImageHeaderAdapter() +
            loadMoreAdapterModule.adapter +
            ProjectLoadMoreState(loadMoreAdapterModule) +
            ProjectEmptyAdapter(loadMoreAdapterModule)

```

在需要特殊的Adapter时，重新组合成新的即可

```kotlin
fun SpecialAdapter(loadMoreAdapterModule: LoadMoreAdapterModule<*, *>) =
    ImageHeaderAdapter() +
            ProjectHeaderAdapter() +
            loadMoreAdapterModule.adapter +
            ProjectLoadMoreState(loadMoreAdapterModule) +
            ProjectEmptyAdapter(loadMoreAdapterModule) +
            ProjectFooterAdapter()

```

### GirdLayoutManager 加载状态单独占用一行

GridLayoutManager 本身提供了SpanSizeLookup来精确设置其跨度

- gridLayoutManager.configFullSpan() 配置某个position 是否需要单独占用一行的情况

需要注意的是RecyclerView/LayoutManager 中的position是全局的，在使用了ConcatAdapter时，提供了一些拓展方法便于转换

- concatAdapter.getAdapterByItemPosition(position) Adapter 可以获取该position所属的Adapter

- concatAdapter.findLocalPositionAt(adapter,globalPosition) 把全局位置转换为子Adapter的相对位置

```kotlin
binding.list.layoutManager = GridLayoutManager(this, 3).apply {
    configFullSpan { concatAdapter.getAdapterByItemPosition(it) != dataAdapter } //除了数据外，其他Adapter单独占用1行
}
```

### StaggeredGridLayoutManager 加载状态单独占用一行

StaggeredGridLayoutManager 中无法精确设置跨度，而是通过StaggeredGridLayoutManager.LayoutParams的isFullSpan属性去设置满跨度

- concatAdapter.adapters 获取ConcatAdapter的所有子Adapter

- adapter.setFullSpan() 设置adapter中的所有item/部分item为满跨度

```kotlin
binding.list.adapter = LoadMoreAdapters.CommonAdapter(loadMoreModule).apply {
    adapters.filterIsInstance<MultiTypeBindingAdapter<Any, ViewBinding>>()
        .filterNot { it == dataAdapter }//除了数据外，其他Adapter单独占用1行
        .forEach { it.setFullSpan() }
}

binding.list.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
```

## Paging3模块

和动态加载模块基本一致，但是需要继承PagingBindingAdapter

## 选择模块（无侵入）

选择模块一般有2种实现，

实现1：标记到Item中增加属性来标记是否选中，这种方式访问速度快，但是需要修改Item类，内存占用较高，侵入性强

实现2：通过使用HashMap记录选中下标,这种方式访问较慢，但无需修改Item类侵入性弱，本库采用此方式实现。

同时，不管是实现1还是实现2：对数据变更的兼容新都不太好。因此增加了通过Item id来保存选中状态。

### 单选

- adapter.setupSingleSelectModule() 单选模块
- selectModule.clearSelected() 清空选择
- selectModule.selectIndex 设置/获取选择下标
- selectModule.doOnSelectChange() 监听选择变化，包括用户点击和代码设置导致的变化
- selectModule.doOnUserSelect() 监听/拦截用户点击选择事件

```kotlin
val selectModule = dataAdapter.setupSingleSelectModule()

selectModule.clearSelected()//清空选择
selectModule.selectIndex = 1 //设置选择下标
selectModule.selectedItem //获取当前选择item
selectModule.doOnSelectChange {
    //监听选择变化
}
selectModule.doOnUserSelect { position, currentSelected -> true }//监听/拦截用户点击选择
```

- adapter.setupSingleSelectModuleByKey() 单选模块，以Item的id来标记选择，来使得数据变化后避免丢失Item

```kotlin
val selectModule = dataAdapter.setupSingleSelectModuleByKey { it.id }
```

### 多选

- dataAdapter.setupMultiSelectModule() 多选模块
- selectModule.clearSelected() 清空选中
- selectModule.selectAll() 全选
- selectModule.invertSelected() 反选
- dataAdapter.setupMultiSelectModuleByKey() 多选模块，以Item的id来标记选择，来使得数据变化后避免丢失Item

```kotlin
val selectModule = dataAdapter.setupMultiSelectModule()
val selectModule = dataAdapter.setupMultiSelectModuleByKey { it.id }

```

### 刷新选中状态ui

如果需要对选中的item进行ui上的变更,在adapter中使用isItemSelected (单选和多选都是)

- viewHolder.isItemSelected 当前Item是否选中

```kotlin
private val adapter = BindingAdapter(ItemTestBinding::inflate, TestData.stringList()) { _, item ->
    itemBinding.tips.setTextColor(if (isItemSelected) Color.BLUE else Color.BLACK)
}
```

## 布局悬浮

### 悬浮Item(无侵入)

- recyclerView.setupStickItemModule() Item 悬浮模块，配置需要悬浮的position

```kotlin
recyclerView.setupStickItemModule() {
    val position = concatAdapter.findLocalPositionAt(adapter1, it)//设置adapter1中的数据每隔10个进行悬浮
    position != RecyclerView.NO_POSITION && (position) % 10 == 0
}

```

注意: 对Item进行悬浮，因为悬浮的Item和列表的Item不是同一个view，因此需要规范绑定数据（将状态存储在Item中，而不是View中），以免同步不到悬浮的View

### 悬浮Item(第三方 StickyHeaderRecyclerView)

adapter 需使用ConcatAdapter（原因不明。。。） ， 需要悬浮的Item 需实现StickyHeader接口

```kotlin
binding.list.layoutManager = StickyLayoutManager(this) { adapter.data }
```

### 悬浮头部布局

某些情况头部布局比较复杂，且并不属于RecyclerView的Item，本库单独实现了一个StickContainerLayout 来帮助此类。

1. 添加StickContainerLayout 到xml 布局中
- `app:stick_scroll_mode=""` 控制滚动头部相比较于内容的优先级（before_scroll_up、before_scroll_down、after_scroll_up、after_scroll_down的组合）
- `app:stick_mode="sticking_all"`  配置多个粘性头部的模式（sticking_all：所有的粘性布局依次显示，sticking_latest：最后一个粘性将上一个顶上去）

- stickContainerLayout.stickMode 配置多个粘性头部的模式，StickingLatest：最后一个粘性将上一个顶上去,StickingAll 所有的粘性布局依次显示

- stickContainerLayout.stickScrollMode 滚动顺序配置：

CONSUME_BEFORE_CONTENT_SCROLL_UP 向上滑动content前，先隐藏header
CONSUME_BEFORE_CONTENT_SCROLL_DOWN 向下滑动content前，先显示header
CONSUME_AFTER_CONTENT_SCROLL_UP 向上滑动content后，再隐藏header
CONSUME_AFTER_CONTENT_SCROLL_DOWN 向下滑动content后，再显示header
CONSUME_PREFER_CONTENT_SHOW 总是优先显示内容，向上时，先隐藏header，向下时，等内容滚动往再显示header（默认）
CONSUME_BEFORE_CONTENT 总是优先操作header

2. 配置子布局

- `app:stick_type="header_scroll"` 需要滚动的头部布局

- `app:stick_type="header_stick"` 需要悬浮的头部布局

- `app:stick_type="content"` 内容布局（必须）

```xml

<me.lwb.adapter.sticklayout.StickContainerLayout
    app:stick_scroll_mode="before_scroll_up|after_scroll_down" app:stick_mode="sticking_all">

    <TextView app:stick_type="header_scroll" />

    <CheckBox app:stick_type="header_stick" />

    <ImageView app:stick_type="header_scroll" />

    <com.google.android.material.tabs.TabLayout app:stick_type="header_stick" />

    <androidx.viewpager2.widget.ViewPager2 app:stick_type="content" />

</me.lwb.adapter.sticklayout.StickContainerLayout>

```

## ViewPager

支持ViewPager2和PagerSnapHelper 模式

### ViewPager2

直接设置到adapter中即可

```kotlin
viewPager2.adapter = adapter
```

如果项目中原来是RecyclerView 需要改造成无限滚动，且自动在activity resume时滚动的ViewPager，可按如下：

### ViewPager模块

利用PagerSnapHelper 实现的 ViewPagerModule

- recyclerView.setupViewPagerModule() 设置recyclerView设置为ViewPager模式

- viewPagerModule.setupAutoScrollModule() 添加自动滚动模块

- autoScrollModule.bindLifecycle() 设置自动滚动模块绑定生命周期

```kotlin
recyclerView.adapter = infiniteAdapter

infiniteAdapter.setupInfiniteDataModule() //adapter设置为无限数据
recyclerView.scrollToPosition(infiniteAdapter.itemCount / 2)//滚动到中间

val viewPagerModule = recyclerView.setupViewPagerModule()
val autoScrollModule = viewPagerModule.setupAutoScrollModule()//设置ViewPager添加自动滚动
autoScrollModule.bindLifecycle(this)//设置自动滚动模块绑定生命周期

```

## 滚轮WheelView

绝大多数的WheelView对自定义布局的支持性都不好。 本库利用RecyclerView实现的WheelView能通过Adapter配置其布局

### 滚轮模块

- recyclerView.setupWheelModule() 设置滚轮模块

- wheelModule.offset 滚轮上下留白的Item数量，(具体宽度以Adapter的第一个Item为主)

- wheelModule.orientation
  方向横向或竖向,RecyclerWheelViewModule.HORIZONTAL/RecyclerWheelViewModule.VERTICAL

- wheelModule.setWheelDecoration() 设置分割线

- wheelModule.flingVelocityFactor fling速度控制

- wheelModule.selectedPosition 获取/设置当前选中

- wheelModule.onScrollingSelectListener 监听当前滚动变化

- wheelModule.onSelectChangeListener 监听当前选中变化

```kotlin
val wheelModule = recyclerView.setupWheelModule()
wheelModule.apply {
    offset = 2 //上下偏移多少
    orientation = RecyclerWheelViewModule.HORIZONTAL//方向
    setWheelDecoration(DefaultWheelDecoration(10.dp, 10.dp, 2.dp, "#dddddd".toColorInt()))//分割线
} 
```

### 刷新选中状态ui

如果需要对选中的item进行ui上的变更,在adapter中使用isWheelItemSelected

- viewHolder.isWheelItemSelected 当前Item是否选中

```kotlin
private val adapter =
    BindingAdapter<String, ItemWheelVerticalBinding>(ItemWheelVerticalBinding::inflate) { _, item ->
        itemBinding.text.setTextColor(
            if (isWheelItemSelected) Color.BLACK else Color.GRAY
        )
    }
```

### 多滚轮联动
LinkageWheelView 帮助多个WheelView进行联动

- linkageWheelView.setAdapterFactory() 设置Adapter工厂，决定了WheelView的样式（必须）
- linkageWheelView.setData() 设置数据，决定了WheelView的数据来源（必须）
- linkageWheelView.currentPositions 获取/设置当前所有滚轮选中位置
- linkageWheelView.currentItems 获取当前所有滚轮选中内容
- linkageWheelView.onSelectListener 当前滚轮选中变化回调 
- linkageWheelView.wheelOffset 滚轮上下留白的Item数量 
- linkageWheelView.wheelDecoration 设置分割线 

```kotlin
linkageWheelView.setAdapterFactory { WheelAdapter() }

//设置每一级的数据加载策略
linkageWheelView.setData {
    provideData { provinces.map { it.name } }
    provideData { provinces[it[0].selectedPosition].cities.map { it.name } }
    provideData { provinces[it[0].selectedPosition].cities[it[1].selectedPosition].counties }
}
```

# 关于拓展模块

其中标注`(无侵入)`的属于独立拓展模块，一般在代码中，以xxx.setupXxxModule(),来插入该模块。

独立拓展模块 依赖BindingAdapter的公开方法，独立拓展模块之间也无相互依赖关系，也不需要调用其私有方法，因此需要拓展时，拷贝一份到自己项目中不会有报错，
也无需修改BindingAdapter内部实现。


