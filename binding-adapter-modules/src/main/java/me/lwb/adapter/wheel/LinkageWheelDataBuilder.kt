package me.lwb.adapter.wheel

import android.os.CancellationSignal
import kotlin.experimental.ExperimentalTypeInference


/**
 * Created by ve3344@qq.com.
 */
class LinkageWheelDataBuilder internal constructor() {
    private val list: MutableList<LinkageWheelView.DataProvider> = ArrayList()
    val data: List<LinkageWheelView.DataProvider> get() = list

    fun provideData(provider: (p: Array<LinkageWheelView.WheelWrapper>) -> List<CharSequence>): LinkageWheelDataBuilder {
        list.add(LinkageWheelView.DataProvider { _, parents, loadCallback ->
            loadCallback(provider(parents))
        })
        return this
    }


    fun provideDataAsync(provider: (c: CancellationSignal, p: Array<LinkageWheelView.WheelWrapper>, loadCallback: (List<CharSequence>) -> Unit) -> Unit): LinkageWheelDataBuilder {
        list.add(LinkageWheelView.DataProvider { c, parents, loadCallback ->
            provider(c, parents, loadCallback)
        })
        return this
    }
}

@OptIn(ExperimentalTypeInference::class)
@BuilderInference
fun LinkageWheelView.setData(build: LinkageWheelDataBuilder.() -> Unit) {
    setData(LinkageWheelDataBuilder().also(build).data)
}


class LinkageWheelDataLazy(
    val size: Int,
    val onLoad: (index: Int, parents: Array<LinkageWheelView.WheelWrapper>) ->Unit
) {
    class LazyDataProvider(val onLoad: (parents: Array<LinkageWheelView.WheelWrapper>) -> Unit) :
        LinkageWheelView.DataProvider {
        var callback: ((Collection<CharSequence>) -> Unit)? = null
            private set

        override fun onLoad(
            cancellationSignal: CancellationSignal,
            parents: Array<LinkageWheelView.WheelWrapper>,
            loadCallback: (Collection<CharSequence>) -> Unit
        ) {
            callback = loadCallback
            onLoad(parents)
            cancellationSignal.setOnCancelListener {
                if (callback === loadCallback) {
                    callback = null
                }
            }
        }
    }
    fun callback(index: Int,collection: Collection<CharSequence>){
        data.getOrNull(index)?.callback?.invoke(collection)
    }

    val data: List<LazyDataProvider>
        get() = (0 until size).map { index ->
            LazyDataProvider {
                onLoad(index, it)
            }
        }
}