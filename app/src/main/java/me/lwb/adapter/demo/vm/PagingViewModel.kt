package me.lwb.adapter.demo.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import me.lwb.adapter.demo.base.ui.pageDataWithCache
import me.lwb.adapter.demo.data.repository.ProjectRepository
import java.lang.RuntimeException

/**
 * Created by ve3344@qq.com.
 */
open class PagingViewModel(application: Application) :
    AndroidViewModel(application) {
    val projectTitle = MutableLiveData<String>()
    val projects = pageDataWithCache { index, _, reachedEnd ->
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