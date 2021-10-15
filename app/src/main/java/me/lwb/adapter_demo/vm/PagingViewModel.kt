package me.lwb.adapter_demo.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import me.lwb.adapter_demo.data.repository.ProjectRepository
import kotlinx.coroutines.delay
import me.lwb.context.AppContext
import me.lwb.adapter_demo.base.ui.pageDataWithCache
import java.lang.RuntimeException

/**
 * Created by ve3344@qq.com on 2021/9/27.
 */
open class PagingViewModel(application: Application = AppContext.context as Application) :
    AndroidViewModel(application) {
    val projectTitle = MutableLiveData<String>()
    val projects = pageDataWithCache { index, count, reachedEnd ->
        if (probability(0.2) && index == 1) {
            reachedEnd()
            return@pageDataWithCache emptyList()
        }
        if (probability(0.2)) {
            //load error
            throw RuntimeException("Load error")
        }
        if (index > 1) {
            //loading
            delay(1000)
        }
        val data = ProjectRepository.getProjects(index, 2).data
        if (data.over) {
            reachedEnd()
        }
        data.datas
    }

    private fun probability(probability: Double) = Math.random() < probability
}