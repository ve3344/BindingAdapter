package me.lwb.adapter_demo.base.ui

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import me.lwb.utils.android.ext.doOnDestroyOnce
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias BindingInflater<V> = (inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean) -> V

typealias BindingBinder<V> = (view: View) -> V


inline fun <A, T : ViewBinding> A.viewBinding(crossinline inflater: BindingInflater<T>) where A : Activity, A : LifecycleOwner =
    lazy(LazyThreadSafetyMode.NONE) {
        inflater(layoutInflater, null, false).also {
            setContentView(it.root)
        }
    }

fun <F, T : ViewBinding> F.viewBinding(creator: BindingBinder<T>): ClearbleLazy<T> where F : Fragment, F : LifecycleOwner =
    ClearbleLazyImpl {
        creator(view ?: throw IllegalStateException("Root view is null"))
    }

inline fun <T : ViewBinding> Dialog.viewBinding(crossinline inflater: BindingInflater<T>) =
    lazy(LazyThreadSafetyMode.NONE) {
        inflater(layoutInflater, null, false).also {
            setContentView(it.root)
        }
    }

inline fun <T : ViewBinding> ViewGroup.viewBinding(
    crossinline inflater: BindingInflater<T>,
    attachToRoot: Boolean = true
) =
    lazy(LazyThreadSafetyMode.NONE) {
        inflater(LayoutInflater.from(context), this, attachToRoot)
    }

inline fun <reified T : ViewBinding> ViewGroup.viewBinding(crossinline creator: BindingBinder<T>) =
    lazy(LazyThreadSafetyMode.NONE) {
        creator(this)
    }

inline fun <A, reified T : ViewBinding> A.viewBinding(): Lazy<T> where A : Activity, A : LifecycleOwner =
    viewBinding(InflateUtils.makeInflate(T::class.java))

inline fun <F, reified T : ViewBinding> F.viewBinding(): ClearbleLazy<T> where F : Fragment, F : LifecycleOwner =
    viewBinding(InflateUtils.makeBinder(T::class.java))


inline fun <reified T : ViewBinding> Dialog.viewBinding() =
    viewBinding(InflateUtils.makeInflate(T::class.java))

inline fun <reified T : ViewBinding> ViewGroup.viewBinding(
    attachToRoot: Boolean = true
) =
    viewBinding(InflateUtils.makeInflate(T::class.java), attachToRoot)


@Suppress("UNCHECKED_CAST")
object InflateUtils {
    fun <VB : ViewBinding> makeInflate(cls: Class<*>): BindingInflater<VB> {
        val method = cls.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return { layoutInflater, viewGroup, attachToRoot ->
            method.invoke(null, layoutInflater, viewGroup, attachToRoot) as VB
        }
    }

    fun <VB : ViewBinding> makeBinder(cls: Class<*>): BindingBinder<VB> {
        val method =
            cls.getDeclaredMethod("bind", View::class.java)
        return { view ->
            method.invoke(null, view) as VB
        }
    }
}

interface ClearbleLazy<VB> : ReadOnlyProperty<Fragment, VB>
private class ClearbleLazyImpl<VB : ViewBinding>(val initializer: () -> VB) :
    ClearbleLazy<VB> {
    private var viewBinding: VB? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        viewBinding?.let {
            return it
        }
        thisRef.viewLifecycleOwner.doOnDestroyOnce {
            viewBinding = null
        }
        return initializer().also { viewBinding = it }
    }

}