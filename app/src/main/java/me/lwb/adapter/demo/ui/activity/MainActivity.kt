package me.lwb.adapter.demo.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityMainBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleTitleBinding
import me.lwb.adapter.ext.buildMultiTypeAdapterByType
import me.lwb.adapter.ext.replaceData

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by viewBinding()

    //配置数据多布局
    val adapter =
        buildMultiTypeAdapterByType {
            //配置item到布局类型的映射
            layout(ItemSimpleTitleBinding::inflate) { _, item: Page.Group ->
                itemBinding.title.text = item.title
            }
            layout(ItemSimpleBinding::inflate) { _, item: PageInfo ->
                itemBinding.title.text = item.title
                itemBinding.root.setOnClickListener {
                    startActivity(Intent(this@MainActivity, item.clazz))
                }
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.list.adapter = adapter

        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        val newData = packageInfo.activities
            .mapNotNull { getPageInfo(it) }
            .groupBy { it.group }
            .toSortedMap()
            .flatMap { listOf(it.key) + it.value }
        adapter.replaceData(newData)
    }

    private fun getPageInfo(it: ActivityInfo): PageInfo? {
        val clz = Class.forName(it.name)
        val page = clz?.getAnnotation(Page::class.java) ?: return null
        return PageInfo(page.group, page.title, clz)
    }

    class PageInfo(val group: Page.Group, val title: String, val clazz: Class<*>)

}
