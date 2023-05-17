package me.lwb.adapter.demo.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import me.lwb.adapter.demo.data.bean.ProjectBean
import me.lwb.adapter.demo.data.repository.ProjectRepository
import me.lwb.adapter.demo.base.ui.PageLoadMoreData
import java.lang.RuntimeException

/**
 * Created by ve3344@qq.com.
 */
open class LoadMoreViewModel(application: Application) :
    AndroidViewModel(application) {
    private val TAG: String = this.javaClass.simpleName

    val projects = PageLoadMoreData<ProjectBean>{

        Log.d(TAG, "PageLoadMore: $it")
        if (probability(0.2) && it.isFirstProgress) {
            return@PageLoadMoreData emptyList()
        }
        if (probability(0.2)) {
            //load error
            throw RuntimeException("Load error")
        }
        if (it.pageIndex > 1) {
            //loading
            delay(1000)
        }
        val data = ProjectRepository.getProjects(it.pageIndex, 2).data
        data.datas
    }


    private fun probability(probability: Double) = Math.random() < probability


    fun reload(){

        projects.reload()
    }
}