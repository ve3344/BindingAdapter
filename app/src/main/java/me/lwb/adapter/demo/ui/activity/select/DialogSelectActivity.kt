package me.lwb.adapter.demo.ui.activity.select

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.HeaderSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.demo.vm.TestData
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.utils.android.ext.toast

@Page("弹窗选择", Page.Group.SELECT)
class DialogSelectActivity : AppCompatActivity() {
    private val binding: ActivitySimpleBinding by viewBinding()
    private val titleAdapter = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)

    val data = TestData.stringList()
    var currentSelect = data.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.actionList.adapter = ActionAdapter(
            "Choose Direct" to Runnable {
                lifecycleScope.launch {
                    setCurrent(awaitChoose(data, currentSelect))
                }
            },
            "Choose With Confirm" to Runnable {
                lifecycleScope.launch {
                    setCurrent(awaitChooseDirect(data, currentSelect))
                }
            }
        )
        binding.list.adapter=titleAdapter

    }

    private fun setCurrent(select: String) {
        toast("You choose $select")

        currentSelect = select
        titleAdapter.update {
            itemBinding.tips.text=select
        }

    }


}


