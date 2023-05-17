package me.lwb.adapter.demo.ui.activity

/**
 * Created by ve3344@qq.com.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Page(val title:String,val group: Group){

    enum class Group(val title:String){
        BASIC("基本"),
        SELECT("选择"),
        LOAD_MORE("分页"),
        STICK("悬浮"),
        PAGING3("分页(Paging3)"),
        NESTED("嵌套"),
        WHEEL("Wheel"),
        VIEW_PAGER("ViewPager"),
    }
}
