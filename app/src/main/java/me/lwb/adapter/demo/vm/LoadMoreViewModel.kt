package me.lwb.adapter.demo.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import me.lwb.adapter.demo.base.ui.PageLoadMoreData
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.data.repository.ProjectRepository

/**
 * Created by ve3344@qq.com.
 */
open class LoadMoreViewModel(application: Application) :
    AndroidViewModel(application) {
    private val TAG: String = this.javaClass.simpleName

    val loadLogs = MutableStateFlow(listOf<String>())

    private fun addLog(log:String){
        loadLogs.value = loadLogs.value + listOf(log)

    }
    private val projectsData = PageLoadMoreData<ProjectBean> {
        Log.d(TAG, "PageLoadMore: $it")
        if (probability(0.2) && it.isFirstProgress) {
            addLog("Load (${it.pageIndex},${it.pageSize}) empty")
            return@PageLoadMoreData emptyList()
        }
        if (probability(0.2)) {
            //load error
            addLog("Load (${it.pageIndex},${it.pageSize}) error")

            throw RuntimeException("Load error")
        }
        if (it.pageIndex > 1) {
            //loading
            addLog("Load (${it.pageIndex},${it.pageSize}) loading")

            delay(1000)
        }
        val data = ProjectRepository.getProjects(it.pageIndex, 2).data
        data.datas.apply {
            addLog("Load (${it.pageIndex},${it.pageSize}) data($size)")

        }
    }

    val projects get() = projectsData.source

    private fun probability(probability: Double) = Math.random() < probability


    fun reload() {

        projectsData.reload()
    }
}