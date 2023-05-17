package me.lwb.adapter.demo.data.common

data class CommonPagingData<I>(
    val curPage:Int,
    val datas: List<I>,
    val pageCount:Int,
    val size:Int,
    val total:Int,
    val offset:Int,
    val over:Boolean
)
typealias CommonPagingResponse<T> = CommonResponse<CommonPagingData<T>>
