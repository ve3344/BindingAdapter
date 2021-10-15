package me.lwb.adapter_demo.data.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import me.lwb.adapter_demo.base.net.MemoryCookieJar
import me.lwb.adapter_demo.base.net.RetrofitApiFactory
import me.lwb.adapter_demo.base.net.lazy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by ve3344@qq.com on 2021/9/3.
 */
object Api {
    private val apiFactory = RetrofitApiFactory(
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .cookieJar(MemoryCookieJar())
                    .build()
            )
            .baseUrl("https://wanandroid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    )


    val projectService: ProjectService by apiFactory.lazy()

}