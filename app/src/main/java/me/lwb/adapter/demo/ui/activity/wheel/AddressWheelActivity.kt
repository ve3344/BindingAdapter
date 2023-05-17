package me.lwb.adapter.demo.ui.activity.wheel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityLinkageWheelBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.utils.android.ext.toast

@Page("联动滚轮-地址选择", Page.Group.WHEEL)
class AddressWheelActivity : AppCompatActivity() {
    val binding by viewBinding(ActivityLinkageWheelBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.linkageWheel.setAdapterFactory { WheelAdapter() }

        val calendarChooseModule =
            binding.linkageWheel.setupAddressChooseModule(AddressChooseModule.loadLocalData(this))


        binding.actionList.adapter = ActionAdapter(
            "set (河北省-唐山市-开平区)" to Runnable {
                calendarChooseModule.current = AddressChooseModule.AddressBean("河北省", "唐山市", "开平区")
            },
            "get current" to Runnable {
                toast(calendarChooseModule.current.toString())
            }
        )
        binding.linkageWheel.onSelectListener = {
            binding.text.text = calendarChooseModule.current.toString()
        }

    }
}