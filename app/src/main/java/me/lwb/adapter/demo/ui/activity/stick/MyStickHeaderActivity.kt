package me.lwb.adapter.demo.ui.activity.stick

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import me.lwb.adapter.demo.R
import me.lwb.adapter.demo.base.ui.viewBinding
import me.lwb.adapter.demo.databinding.ActivityMyStickHeaderBinding
import me.lwb.adapter.demo.databinding.ActivitySimpleBinding
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.demo.ui.activity.Page
import me.lwb.adapter.BindingAdapter


@Page("Header悬停", Page.Group.STICK)
class MyStickHeaderActivity : AppCompatActivity() {
    val binding by viewBinding(ActivityMyStickHeaderBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.setOnRefreshListener {
            binding.root.postDelayed({
                binding.root.isRefreshing = false
            }, 2000)
        }
        val tabs = arrayOf("关注", "热门", "最新")

        binding.pager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = tabs.size
            override fun createFragment(position: Int) = TestFragment()
        }

        val mediator =
            TabLayoutMediator(binding.tab, binding.pager2) { tab, position ->
                tab.text = tabs[position]
            }
        mediator.attach()


    }

    class TestFragment : Fragment(R.layout.activity_simple) {
        val binding: ActivitySimpleBinding by viewBinding(ActivitySimpleBinding::bind)
        private val dataAdapter =
            BindingAdapter(
                ItemSimpleBinding::inflate,
                (0..100).map { it.toString() }) { _, item ->
                itemBinding.title.text = item
            }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.list.adapter = dataAdapter
        }
    }
}