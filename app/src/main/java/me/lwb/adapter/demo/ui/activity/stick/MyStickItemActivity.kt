package me.lwb.adapter.demo.ui.activity.stick

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import me.lwb.adapter.SingleViewBindingAdapter
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.*
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.demo.ui.activity.select.awaitChooseDirect
import me.lwb.adapter.demo.ui.adapter.ActionAdapter
import me.lwb.adapter.ext.*
import me.lwb.adapter.stick.setupStickItemModule
import me.lwb.utils.android.ext.toast

@Page("Item悬停", Page.Group.STICK)
class MyStickItemActivity : AppCompatActivity() {
    private val binding: ActivityMyStickItemBinding by viewBinding()

    sealed interface Item {
        class StickTitle(val title: String, var select: Boolean = false) : Item
        class NormalItem(val text: String) : Item
    }

    private val header1 = SingleViewBindingAdapter(HeaderSimpleBinding::inflate)
    private val header2 = header1.copy()
    private val adapter1 =
        buildMultiTypeAdapterByType {
            //粘性布局
            layout<Item.StickTitle, ItemStickItemTitleBinding>(ItemStickItemTitleBinding::inflate) { position, item ->
                itemBinding.title.text = item.title
                itemBinding.checkbox.isClickable = false
                itemBinding.checkbox.isFocusable = false
                itemBinding.checkbox.isFocusableInTouchMode = false
                itemBinding.root.setOnClickListener {
                    item.select = !item.select
                    it.post {
                        adapter.notifyItemChanged(position)
                    }
                }
                itemBinding.checkbox.isChecked = item.select
            }
            //普通布局
            layout<Item.NormalItem, ItemStickItemNormalBinding>(ItemStickItemNormalBinding::inflate) { _, item ->
                itemBinding.text.text = item.text
                itemBinding.root.setBackgroundColor(Color.WHITE)
                itemBinding.root.setOnClickListener {
                    toast("Item click ${item.text}")
                }
            }
        }

    val footer =
        SingleViewBindingAdapter(FooterSimpleBinding::inflate)

    private val concatAdapter = header1 + header2 + adapter1 + footer

    private var from = 0


    enum class LayoutEnum(val desc: String) {
        LINEAR_VERTICAL("LinearLayoutManager"),
        GRID_VERTICAL("GridLayoutManager"),
        STAGGERED_GRID_VERTICAL("StaggeredGridLayoutManager"),
    }

    var currentLayout = LayoutEnum.LINEAR_VERTICAL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter1.setFullSpan {
            (it) % 10 == 0
        }


        binding.actionList.adapter = ActionAdapter(
            "更换布局" to Runnable { changeLayout() },
            "添加数据" to Runnable { addData() }
        )
        setLayout(currentLayout)
        addData()
    }

    private fun changeLayout() {
        lifecycleScope.launch {
            val res =
                awaitChooseDirect(LayoutEnum.values().map { it.desc }, currentLayout.desc)
            setLayout(LayoutEnum.values().first { it.desc.equals(res, false) })
        }
    }

    private fun addData() {
        val appendData = (from..from + 100).map { r ->
            if (r % 10 == 0) Item.StickTitle("$r") else Item.NormalItem("$r" + "\n".repeat((0..4).random()))
        }
        adapter1.appendData(appendData)
    }

    private fun setLayout(layout: LayoutEnum) {
        binding.listContainer.removeAllViews()

        val list = RecyclerView(this).also { binding.listContainer.addView(it) }
        list.adapter = concatAdapter

        when (layout) {
            LayoutEnum.LINEAR_VERTICAL -> {
                list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            }
            LayoutEnum.GRID_VERTICAL -> {
                list.layoutManager =
                    GridLayoutManager(this, 3, RecyclerView.VERTICAL, false).apply {
                        configFullSpan {
                            val positionAt = concatAdapter.findLocalPositionAt(adapter1, it)
                            positionAt != RecyclerView.NO_POSITION && (positionAt) % 10 == 0
                        }
                    }
            }
            LayoutEnum.STAGGERED_GRID_VERTICAL -> {
                list.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            }
        }
        list.setupStickItemModule() {
            val positionAt = concatAdapter.findLocalPositionAt(adapter1, it)
            positionAt != RecyclerView.NO_POSITION && (positionAt) % 10 == 0
        }


    }
}