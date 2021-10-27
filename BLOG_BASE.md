# 使用binding-adapter告别新建Adapter和ViewHolder

binding-adapter 是一个使用ViewBinding/DataBinding 直接生成RecyclerView Adapter的库。

将Adapter扁平化，不用创建Adapter类，避免传输Item点击事件、Item长按事件、元素点击事件等，直接在Activity中处理。

项目地址[binding-adapter](https://github.com/ve3344/binding-adapter)

# 特点
- 无需创建ViewHolder，无需创建Adapter

- 直接操作View，再也不需要View id以及findViewById
  
- 支持ViewBinding、DataBinding

- 支持下拉刷新

- 支持Item的点击事件监听

- 支持添加任意Header和Footer

- 支持多布局配置

- 支持Paging3下拉加载更多

- 支持添加加载状态（加载中、无更多数据、加载失败、加载完成）

- 支持加载失败点击重试

- 支持没有数据时显示空布局

- 支持折叠和展开items

- 支持隐藏item(不占用布局位置)

- 支持GirdLayoutManager

- 强大的拓展性和自由度

- 不依赖任何第三方库，轻量（方法数<40，体积<23kb）

- 无反射

- 极简的设计，所见即所得


# 安装

#### 添加仓库

```groovy
//in build.gradle(Project)
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

// 新版方式 settings.gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

#### 添加依赖

```groovy
//in build.gradle(module)
dependencies {
    //core
    implementation "com.github.ve3344.binding-adapter:binding-adapter:1.0.0"
    //paging support
    implementation "com.github.ve3344.binding-adapter:binding-adapter-paging:1.0.0"
}
```


#### 基本使用
1. 开启ViewBinding/DataBinding
```groovy
android {
    ...
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}
```
2. 创建Item的布局`item_page.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <Button
        android:id="@+id/title"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```
开启ViewBinding/DataBinding后，会自动生成ItemPageBinding文件

3. 生成Adapter

生成Adapter需要3个主要参数,布局(即刚刚生成的ItemPageBinding),初始数据列表(可为空),布局和元素的绑定器。

在绑定器内，配置对ui元素和bean属性之间的绑定，设置点击事件等。

如果使用DataBinding 记得在绑定器内调用executePendingBindings()方法

```kotlin
val list = listOf(
    PageItemBean("Simple", MutableActivity::class.java),
)
val adapter=BindingAdapter(ItemPageBinding::inflate, list) { position, item ->
    binding.title.text = item.title
    binding.title.setOnClickListener {
        startActivity(Intent(this@MainActivity, item.clz))
    }
}
```

#### 添加Header和Footer

1. 创建Header布局`header_simple`如下，得到HeaderSimpleBinding。同理创建Footer布局得到FooterSimpleBinding。

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:layout_gravity="center_horizontal"
    android:gravity="center"
    android:background="#DDDDDD"
    >
    <TextView
        android:id="@+id/tips"
        android:layout_width="match_parent"
        android:text="Header"
        android:gravity="center"
        android:layout_height="match_parent"/>
</LinearLayout>
```

2. 分别使用SingleViewBindingAdapter生成单个View的Adapter，然后直接使用`+`拼接。

拼接顺序即Header、内容、Footer的相对位置。

得益于ConcatAdapter的灵活性，你可以在任意地方添加布局，而不仅仅是Header、Footer


```kotlin
val header = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)
val footer = SingleViewBindingAdapter(FooterSimpleBinding::inflate)

binding.list.adapter = header + adapter + footer
```
#### 控制列表元素隐藏和显示

BindingAdapter 添加了isVisible 属性，直接控制元素的隐藏和显示。

```kotlin
adapter.isVisible = isChecked
```

