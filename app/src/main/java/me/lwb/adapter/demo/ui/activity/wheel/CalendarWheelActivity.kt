package me.lwb.adapter.demo.ui.activity.wheel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityLinkageWheelBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.utils.android.ext.toast
import java.text.SimpleDateFormat
import java.util.*

@Page("联动滚轮-年月日选择", Page.Group.WHEEL)
class CalendarWheelActivity : AppCompatActivity() {
    val binding by viewBinding(ActivityLinkageWheelBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.linkageWheel.setAdapterFactory { WheelAdapter() }

        val minValue = Calendar.getInstance().apply { set(2015, Calendar.JUNE, 10) }
        val calendarChooseModule =
            binding.linkageWheel.setupCalendarChooseModule(CalendarChooseModule.Options(minValue = minValue))


        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        binding.actionList.adapter = ActionAdapter(
            "set now" to Runnable {
                calendarChooseModule.current = Calendar.getInstance()
            },
            "set 2022-02-02" to Runnable {
                calendarChooseModule.current =
                    Calendar.getInstance().apply { time = Date(sdf.parse("2022-02-02")!!.time) }
            },
            "get current" to Runnable {
                toast(sdf.format(calendarChooseModule.current.time))
            }
        )
        binding.linkageWheel.onSelectListener = {
            binding.text.text = sdf.format(calendarChooseModule.current.time)
        }

    }
}