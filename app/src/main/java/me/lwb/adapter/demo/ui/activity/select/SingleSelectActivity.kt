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
import me.lwb.adapter.select.AbstractSingleSelectModule
import me.lwb.adapter.select.isItemSelected
import me.lwb.adapter.select.setupSingleSelectModule

/**
 * 单选布局
 */
@Page("单选", Page.Group.SELECT)
class SingleSelectActivity : AppCompatActivity() {
    private val binding: ActivitySimpleBinding by viewBinding()
    private val titleAdapter = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)

    private val dataAdapter =
        BindingAdapter(ItemTestBinding::inflate, TestData.stringList()) { _, item ->
            itemBinding.tips.text = item
            itemBinding.tips.setTextColor(if (isItemSelected) Color.BLUE else Color.BLACK)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectModule: AbstractSingleSelectModule<String, *> = dataAdapter.setupSingleSelectModule()

        binding.list.itemAnimator = null
        binding.list.adapter = titleAdapter + dataAdapter

        selectModule.doOnSelectChange {
            titleAdapter.update {
                itemBinding.tips.text = "key = $it ,item = ${selectModule.selectedItem}"
            }
        }
        binding.actionList.adapter = ActionAdapter(
            "清空选中" to Runnable {
                selectModule.clearSelected()
            },
            "允许点击取消选中" to Runnable {
                selectModule.enableUnselect = true
            },

            )

    }
}