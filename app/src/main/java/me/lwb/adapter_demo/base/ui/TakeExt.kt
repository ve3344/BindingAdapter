package me.lwb.adapter_demo.base.ui

/**
 * Created by luowenbin on 2021/10/14.
 */
public inline fun <reified T> Any?.isType(action: (T) -> Unit) {
    if (this is T){
        action(this)
    }
}