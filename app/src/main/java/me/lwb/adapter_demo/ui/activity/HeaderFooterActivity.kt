package me.lwb.adapter_demo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.lwb.adapter_demo.base.ui.viewBinding
import me.lwb.adapter_demo.databinding.ActivityHeaderFooterBinding
import me.lwb.adapter_demo.databinding.FooterSimpleBinding
import me.lwb.adapter_demo.databinding.HeaderSimpleBinding
import me.lwb.adapter_demo.databinding.ItemSimpleBinding
import me.lwb.bindingadapter.BindingAdapter
import me.lwb.bindingadapter.SingleViewBindingAdapter
import me.lwb.bindingadapter.plus
import me.lwb.utils.android.utils.IntervalUtils
import java.util.*

class HeaderFooterActivity : AppCompatActivity() {
    private val binding: ActivityHeaderFooterBinding by viewBinding()
    val chooses = listOf("a", "b")
    private val adapter =
        BindingAdapter(ItemSimpleBinding::inflate, chooses) { position, item ->
            binding.title.text = item
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val header = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)
        val footer = SingleViewBindingAdapter(FooterSimpleBinding::inflate)

        binding.list.adapter = header + adapter + footer


        //更新header
        lifecycleScope.launchWhenResumed {
            while (true){
                header.bindingContent?.tips?.text="Time:${Date()}"
                delay(1000)
            }

        }
    }

}