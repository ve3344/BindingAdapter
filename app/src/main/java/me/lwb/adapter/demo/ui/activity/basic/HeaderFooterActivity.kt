package me.lwb.adapter.demo.ui.activity.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityHeaderFooterBinding
import me.lwb.adapter.demo.databinding.FooterSimpleBinding
import me.lwb.adapter.demo.databinding.HeaderSimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.TestData
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.ext.plus
import java.util.*
@Page("Header和Footer", Page.Group.BASIC)
class HeaderFooterActivity : AppCompatActivity() {
    private val binding: ActivityHeaderFooterBinding by viewBinding()

    private val chooses = TestData.stringList()
    private val adapter =
        BindingAdapter(ItemSimpleBinding::inflate, chooses) { _, item ->
            itemBinding.title.text = item
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val header = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)
        val footer = SingleViewBindingAdapter(FooterSimpleBinding::inflate)

        binding.list.adapter = header + adapter + footer


        //更新header
        lifecycleScope.launchWhenResumed {
            while (true){
                header.update {
                    itemBinding.tips.text="Time:${Date()}"
                }
                delay(1000)
            }

        }
    }

}