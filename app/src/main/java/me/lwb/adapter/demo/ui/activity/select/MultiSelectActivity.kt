package me.lwb.adapter.demo.ui.activity.select

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.HeaderSimpleBinding
import me.lwb.adapter.demo.databinding.ItemTestBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.demo.vm.TestData
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.ext.plus
import me.lwb.adapter.select.AbstractMultiSelectModule
import me.lwb.adapter.select.isItemSelected
import me.lwb.adapter.select.setupMultiSelectModuleByKey

@Page("多选", Page.Group.SELECT)
class MultiSelectActivity : AppCompatActivity() {
    private val binding: ActivitySimpleBinding by viewBinding()
    private val titleAdapter = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)

    private val dataAdapter =
        BindingAdapter(ItemTestBinding::inflate, TestData.stringList()) { _, item ->
            itemBinding.tips.text = item
            itemBinding.tips.setTextColor(if (isItemSelected) Color.BLUE else Color.BLACK)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectModule: AbstractMultiSelectModule<String, *> = dataAdapter.setupMultiSelectModuleByKey()
        binding.list.itemAnimator = null
        binding.list.adapter = titleAdapter + dataAdapter

        selectModule.doOnSelectChange {
            titleAdapter.update {
                itemBinding.tips.text = "keys = $it ,items = ${selectModule.selectKeys}"
            }

        }

        binding.actionList.adapter = ActionAdapter(
            "清空选中" to Runnable {
                selectModule.clearSelected()
            },
            "全选" to Runnable {
                selectModule.selectAll()
            },
            "反选" to Runnable {
                selectModule.invertSelected()
            },

            )

    }
}