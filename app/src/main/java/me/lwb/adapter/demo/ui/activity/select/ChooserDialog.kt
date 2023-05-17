package me.lwb.adapter.demo.ui.activity.select

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.suspendCancellableCoroutine
import me.lwb.adapter.demo.base.ui.showAlertDialog
import me.lwb.adapter.demo.databinding.ItemSimpleBinding
import me.lwb.adapter.BindingAdapter
import me.lwb.adapter.select.isItemSelected
import me.lwb.adapter.select.setupSingleSelectModule
import me.lwb.utils.android.ext.toast
import me.lwb.utils.ext.map
import kotlin.coroutines.resume

class ChooserDialog(
    private val context: Context,
    private val hasConfirm: Boolean,
    private val chooses: List<String>,
    private val currentSelect: String
) {

    private val dataAdapter =
        BindingAdapter(ItemSimpleBinding::inflate, chooses) { _, item ->
            itemBinding.title.text = item
            itemBinding.root.setBackgroundColor(isItemSelected.map(Color.LTGRAY, Color.WHITE))
        }


    suspend fun awaitChoose() = suspendCancellableCoroutine<String> { c ->
        var dialog: AlertDialog? = null

        dialog = context.showAlertDialog {
            setTitle("Please choose")
            setView(RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = dataAdapter
            })
            val selectModule = dataAdapter.setupSingleSelectModule()
            selectModule.selectIndex=dataAdapter.data.indexOf(currentSelect)

            if (hasConfirm) {
                setPositiveButton("Confirm") { _, _ ->
                    val select = selectModule.selectedItem
                        ?: return@setPositiveButton context.toast("Must select")
                    c.resume(select)
                    dialog?.dismiss()
                }
            } else {
                selectModule.doOnUserSelect { position, currentSelected ->
                    c.resume(dataAdapter.data[position])
                    dialog?.dismiss()
                    false
                }
            }

            setOnCancelListener {
                c.cancel()
            }
        }
        c.invokeOnCancellation { dialog.dismiss() }

    }

}

suspend fun Context.awaitChoose(data: List<String>, currentSelect: String) =
    ChooserDialog(this, true, data,currentSelect).awaitChoose()

suspend fun Context.awaitChooseDirect(data: List<String>, currentSelect: String) =
    ChooserDialog(this, false, data, currentSelect).awaitChoose()

