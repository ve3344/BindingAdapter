package me.lwb.adapter.demo.ui.activity.viewpager

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityViewPagerBinding
import me.lwb.adapter.demo.databinding.ItemPagerBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.ext.copy
import me.lwb.adapter.basic.setupInfiniteDataModule
import me.lwb.adapter.viewpager.setupAutoScrollModule
import me.lwb.adapter.viewpager.setupViewPagerModule

@Page("ViewPager", Page.Group.VIEW_PAGER)
class ViewPagerActivity : AppCompatActivity() {
    private val binding: ActivityViewPagerBinding by viewBinding()


    private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE)

    private val dataAdapter = BindingAdapter(ItemPagerBinding::inflate, colors) { pos, item ->
        itemBinding.image.setBackgroundColor(item)
        itemBinding.text.text = "$pos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.listPager.adapter = dataAdapter.copy()


        val recyclerView = binding.listPager2

        val infiniteAdapter = dataAdapter.copy()
        infiniteAdapter.setupInfiniteDataModule() //adapter设置为无限数据
        recyclerView.adapter= infiniteAdapter
        recyclerView.scrollToPosition(infiniteAdapter.itemCount / 2)//滚动到中间
        val viewPagerModule = recyclerView.setupViewPagerModule()//recyclerView设置为ViewPager
        val autoScrollModule = viewPagerModule.setupAutoScrollModule()//设置ViewPager添加自动滚动
        autoScrollModule.bindLifecycle(this)//设置自动滚动模块绑定生命周期


        binding.pager2.adapter = dataAdapter.copy()

        val infiniteAdapter2 = dataAdapter.copy().apply { setupInfiniteDataModule() }
        binding.infinitePager2.adapter = infiniteAdapter2
        binding.infinitePager2.setCurrentItem(infiniteAdapter2.itemCount / 2, false)
    }


}