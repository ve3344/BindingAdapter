package me.lwb.adapter_demo.base.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding

/**
 * Created by luowenbin on 2021/10/15.
 */
inline fun Context.buildAlertDialog(block: AlertDialog.Builder.() -> Unit): AlertDialog {
    return AlertDialog.Builder(this).apply(block).create()
}
fun <V : ViewBinding> Dialog.setContentView(
    creator: (inflater: LayoutInflater) -> V,
    params: ViewGroup.LayoutParams? = null,
    builder: V.() -> Unit
) {
    if (params == null) {
        setContentView(creator(layoutInflater).apply(builder).root)
    } else {
        setContentView(creator(layoutInflater).apply(builder).root, params)
    }
}