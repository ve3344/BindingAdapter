package me.lwb.adapter_demo.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.CycleInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import me.lwb.adapter_demo.base.ui.viewBinding
import me.lwb.adapter_demo.databinding.ActivityMainBinding
import me.lwb.adapter_demo.databinding.ItemPageBinding
import me.lwb.bindingadapter.BindingAdapter

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by viewBinding()
    val list = listOf(
        PageItemBean("Simple", MutableActivity::class.java),
        PageItemBean("Dialog", DialogSelectActivity::class.java),
        PageItemBean("Paging", PagingActivity::class.java),
        PageItemBean("HeaderFooter", HeaderFooterActivity::class.java),
        PageItemBean("Expand", ExpandActivity::class.java),
        PageItemBean("MultiType", MultiTypeActivity::class.java),
    )
    val adapter=BindingAdapter(ItemPageBinding::inflate, list) { position, item ->
        binding.title.text = item.title
        binding.title.setOnClickListener {
            startActivity(Intent(this@MainActivity, item.clz))
        }
    }

    class PageItemBean(val title: String, val clz: Class<out Activity>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.list.adapter =adapter


    }
}
