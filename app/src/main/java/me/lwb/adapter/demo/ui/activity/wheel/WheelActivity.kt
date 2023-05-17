package me.lwb.adapter.demo.ui.activity.wheel

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityWheelBinding
import me.lwb.adapter.demo.databinding.ItemWheelHorizontalBinding
import me.lwb.adapter.demo.databinding.ItemWheelVerticalBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.ext.replaceData
import me.lwb.adapter.wheel.RecyclerWheelViewModule
import me.lwb.adapter.wheel.decoration.DefaultWheelDecoration
import me.lwb.adapter.wheel.isWheelItemSelected
import me.lwb.adapter.wheel.setupWheelModule
import me.lwb.utils.android.ext.dp

@Page("滚轮", Page.Group.WHEEL)
class WheelActivity : AppCompatActivity() {
    val binding by viewBinding(ActivityWheelBinding::inflate)

    private val adapterVertical =
        BindingAdapter<String, ItemWheelVerticalBinding>(ItemWheelVerticalBinding::inflate) { _, item ->
            itemBinding.text.text = item
            itemBinding.text.setLines(item.length)
            itemBinding.text.setTextColor(
                if (isWheelItemSelected) Color.BLACK else Color.GRAY
            )
        }
    private val adapterHorizontal = BindingAdapter<String, ItemWheelHorizontalBinding>(
        ItemWheelHorizontalBinding::inflate
    ) { _, item ->
        itemBinding.text.text = item
        itemBinding.text.setTextColor(
            if (isWheelItemSelected) Color.BLACK else Color.GRAY
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVertical()

        initHorizontal()


    }

    private fun initHorizontal() {
        binding.wheelHorizontal.adapter = adapterHorizontal
        val wheelModule = binding.wheelHorizontal.setupWheelModule()
        wheelModule.apply {
            offset = 2
            orientation = RecyclerWheelViewModule.HORIZONTAL
            setWheelDecoration(DefaultWheelDecoration(10.dp, 10.dp, 2.dp, "#dddddd".toColorInt()))
        }

        adapterHorizontal.replaceData((0..9).map { it.toString() })
    }

    private fun initVertical() {
        binding.wheelVertical.adapter = adapterVertical
        val wheelModule = binding.wheelVertical.setupWheelModule()
        wheelModule.apply {
            offset = 1
            orientation = RecyclerWheelViewModule.VERTICAL
            setWheelDecoration(DefaultWheelDecoration(10.dp, 10.dp, 2.dp, "#dddddd".toColorInt()))
            onSelectChangeListener = {
                Exception("onSelectChangeListener $it").printStackTrace()
            }
        }

        adapterVertical.replaceData((6..14).map { it.toString() })

        binding.actionList.adapter = ActionAdapter(
            "Set next" to Runnable {
                wheelModule.selectedPosition = wheelModule.selectedPosition + 1
            },
            "Set first" to Runnable {
                wheelModule.selectedPosition = 0
            },
            "Set last" to Runnable {
                wheelModule.selectedPosition = adapterVertical.data.lastIndex
            }
        )
    }
}