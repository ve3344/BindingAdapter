package me.lwb.adapter.demo.data.service

import me.lwb.adapter.demo.data.common.CommonPagingResponse
import me.lwb.adapter.demo.data.bean.ProjectBean
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProjectService {
    @GET("/project/list/{page}/json")
    suspend fun getProjects(
        @Path("page") page: Int,
        @Query("cid") id: Int
    ): CommonPagingResponse<ProjectBean>
}
