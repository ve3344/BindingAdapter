package me.lwb.adapter.demo.ui.activity.basic

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleTitleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.vm.PagingViewModel
import me.lwb.adapter.ext.appendData
import me.lwb.adapter.ext.buildMultiTypeAdapterByIndex
import me.lwb.adapter.ext.buildMultiTypeAdapterByMap
import me.lwb.adapter.ext.buildMultiTypeAdapterByType

@Page("多布局", Page.Group.BASIC)
open class MultiTypeActivity : AppCompatActivity() {
    val vm: PagingViewModel by viewModels()
    val binding: ActivitySimpleBinding by viewBinding()

    sealed class DataType(val text: String) {
        class TitleData(text: String) : DataType(text)
        class NormalData(text: String) : DataType(text)
    }

    //配置数据多布局
    val adapter =
        buildMultiTypeAdapterByType {
            //配置item到布局类型的映射
            layout(ItemSimpleTitleBinding::inflate) { _, item: DataType.TitleData ->
                itemBinding.title.text = item.text
            }
            layout(ItemSimpleBinding::inflate) { _, item: DataType.NormalData ->
                itemBinding.title.text = item.text
            }

        }

    val adapter2 = buildMultiTypeAdapterByMap<DataType> {
        val typeTitle = 0
        val typeNormal = 1
        layout(typeTitle, ItemSimpleTitleBinding::inflate) { _, item: DataType.TitleData ->
            itemBinding.title.text = item.text
        }
        layout(typeNormal, ItemSimpleBinding::inflate) { _, item: DataType.NormalData ->
            itemBinding.title.text = item.text
        }
        extractItemViewType { _, item -> if (item is DataType.TitleData) typeTitle else typeNormal }

    }

    val adapter3 = buildMultiTypeAdapterByIndex {
        val typeTitle = layout(ItemSimpleTitleBinding::inflate) { _, item: DataType.TitleData ->
            itemBinding.title.text = item.text
        }
        val typeNormal = layout(ItemSimpleBinding::inflate) { _, item: DataType.NormalData ->
            itemBinding.title.text = item.text
        }
        extractItemViewType { position, _ -> if (position % 10 == 0) typeTitle else typeNormal }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.list.adapter = adapter3

        adapter3.appendData(
            (0..100).map {
                if (it % 10 == 0) DataType.TitleData("Title $it") else DataType.NormalData("Normal $it")
            }

        )
    }

}