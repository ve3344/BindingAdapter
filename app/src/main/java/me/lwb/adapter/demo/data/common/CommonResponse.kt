package me.lwb.adapter.demo.data.common

import me.lwb.adapter.demo.base.net.IResponse


/**
 * Created by ve3344@qq.com.
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