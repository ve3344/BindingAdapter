package me.lwb.adapter.demo.data.repository

import me.lwb.adapter.demo.data.service.Api.projectService

object ProjectRepository {
    suspend fun getProjects(page: Int, id: Int) = projectService.getProjects(page, id)
}
