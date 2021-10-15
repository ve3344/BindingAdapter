package me.lwb.adapter_demo.base.net

interface IResponse<T> {
    val status: String
    val msg: String?
    val data: T
    val succeed: Boolean
}