package me.lwb.adapter_demo.ui.activity

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import me.lwb.adapter_demo.base.ui.buildAlertDialog
import me.lwb.adapter_demo.base.ui.viewBinding
import me.lwb.adapter_demo.databinding.ActivityDialogBinding
import me.lwb.adapter_demo.databinding.ItemSimpleBinding
import me.lwb.bindingadapter.BindingAdapter
import me.lwb.utils.android.ext.awaitClick
import me.lwb.utils.android.ext.toast
import me.lwb.utils.ext.map
import kotlin.coroutines.resume

class DialogSelectActivity : AppCompatActivity() {
    companion object {
        private const val NOT_SELECTED = -1
    }

    private val binding: ActivityDialogBinding by viewBinding()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            while (true) {
                binding.text.awaitClick()
                val chooses = listOf("A", "B", "C", "D")
                val ret = Chooser(this@DialogSelectActivity, chooses).awaitChoose()
                toast("You choose ${chooses.getOrNull(ret)}")
            }

        }

    }


    class Chooser(private val context: Context, private val chooses: List<String>) {
        private var selectedPosition = NOT_SELECTED

        private val adapter =
            BindingAdapter(ItemSimpleBinding::inflate, chooses) { position, item ->
                binding.title.text = item
                binding.root.setBackgroundColor(
                    (selectedPosition == position).map(
                        Color.LTGRAY,
                        Color.WHITE
                    )
                )
                binding.root.setOnClickListener {
                    onSelectChange(position)
                }
            }

        private fun onSelectChange(position: Int) {
            selectedPosition.also {
                selectedPosition = position
                adapter.notifyItemChanged(it)
                adapter.notifyItemChanged(position)
            }

        }

        suspend fun awaitChoose() = suspendCancellableCoroutine<Int> { c ->

            selectedPosition = NOT_SELECTED
            context.buildAlertDialog {

                val recyclerView = RecyclerView(context)
                setView(recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter

                setTitle("Please choose")
                setPositiveButton("Confirm") { _, _ ->
                    c.resume(selectedPosition)
                }
                setOnCancelListener {
                    c.resume(NOT_SELECTED)
                }
            }.show()
        }

    }


}