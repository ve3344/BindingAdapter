# 使用ViewBinding 简化RecyclerView多布局
项目地址[binding-adapter](https://github.com/ve3344/binding-adapter)

1. 创建好各个类型的xml布局文件

2. 创建多布局配置
    使用MultiTypeAdapterUtils.createConfig创建多布局配置。

    2.1 创建时需要指定一个公共Item类型，只有一种Item可直接使用Item类型，多种Item如果有公共的类可以使用其公共类型或者Any类型。

    2.2 通过*extractItemViewType方法*配置item到布局的映射关系，这个步骤是必须的，返回内容为非负的Int类型，代表布局类型type。这里示例使用了position来映射，偶数为type=0，奇数为type=1。

    2.3 通过*layout方法*配置各个type的布局绑定器，配置内容和单布局的Adapter类似。
    
    注意: layout方法的*顺序与type一一对应*，并且*不允许缺省*。如extractItemViewType方法返回type范围为0、1，则layout方法必须也有2个，且第一个layout方法对应type=0，和第二个layout方法对应type=1。layout方法item的类型存在*强制转换*，需要自行保证类型和type的对应关系正确，如想自行处理转换请使用Any?作为item的类型。


```kotlin
val adapter = MultiTypeAdapterUtils.createConfig<ProjectBean> {
    //配置item到布局类型的映射(必须)
    extractItemViewType { position, item ->
        //type要求从0开始，并且连续
        position % 2 //根据position 分别映射0，1
    }

    //layout按顺序 匹配type

    //type=0
    layout(ItemProject2Binding::inflate) { _, item: ProjectBean ->
        binding.vm = item
        binding.executePendingBindings()
    }

    //type=1
    layout(ItemProjectBinding::inflate) { _, item: ProjectBean ->
        binding.vm = item
        binding.executePendingBindings()
    }

}.asPagingAdapter()


```

3. 使用asPagingAdapter() 或asAdapter() 把多布局配置生成相应RecyclerView Adapter

