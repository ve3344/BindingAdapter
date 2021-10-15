package me.lwb.adapter_demo.data.common

import me.lwb.adapter_demo.base.net.IResponse


/**
 * Created by ve3344@qq.com on 2021/9/3.
 */
data class CommonResponse<T>(
    var errorCode: Int,
    var errorMsg: String?,
    override val data: T
) : IResponse<T> {
    override val status: String
        get() = errorCode.toString()
    override val msg: String?
        get() = errorMsg

    override val succeed: Boolean
        get() = errorCode == 0
}