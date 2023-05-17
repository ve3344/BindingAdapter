package me.lwb.adapter.demo.base.net

import retrofit2.Retrofit
import kotlin.reflect.KProperty

interface IApiFactory {
    fun <T> createApi(clazz: Class<T>): T
}

inline fun <reified T> IApiFactory.lazy(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED): Lazy<T> =
    lazy(mode) { createApi(T::class.java) }

inline operator fun <T, reified V> IApiFactory.getValue(thisRef: T, property: KProperty<*>): V {
    return createApi(V::class.java)
}

class RetrofitApiFactory(private val retrofit: Retrofit) : IApiFactory {
    override fun <T> createApi(clazz: Class<T>): T = retrofit.create(clazz)
}
