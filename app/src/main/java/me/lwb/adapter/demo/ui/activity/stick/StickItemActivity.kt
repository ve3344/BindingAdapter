package me.lwb.adapter.demo.ui.activity.stick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.brandongogetap.stickyheaders.StickyLayoutManager
import com.brandongogetap.stickyheaders.exposed.StickyHeader
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityStickItemBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.databinding.ItemTestBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.ext.appendData
import me.lwb.adapter.ext.buildMultiTypeAdapterByType
@Page("Item悬停（第三方）", Page.Group.STICK)
class StickItemActivity : AppCompatActivity() {

    sealed interface Item {
        class StickTitle(val title: String) : Item, StickyHeader
        class ProjectItem(val text: String) : Item
    }

    val binding: ActivityStickItemBinding by viewBinding()

    //配置数据多布局
    val adapter =
        buildMultiTypeAdapterByType {
            //粘性布局
            layout<Item.StickTitle, ItemTestBinding>(ItemTestBinding::inflate) { _, item ->
                itemBinding.tips.text = item.title
            }
            //普通布局
            layout<Item.ProjectItem, ItemSimpleBinding>(ItemSimpleBinding::inflate) { _, item ->
                itemBinding.title.text = item.text
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.list.layoutManager = StickyLayoutManager(this) { adapter.data }
        binding.list.adapter = ConcatAdapter(adapter)

        adapter.appendData((0..100).map { r ->
            if (r % 10 == 0) Item.StickTitle("$r") else Item.ProjectItem("$r")
        })

    }

}