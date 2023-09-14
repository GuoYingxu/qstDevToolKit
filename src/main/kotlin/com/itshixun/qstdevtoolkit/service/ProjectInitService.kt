package com.itshixun.qstdevtoolkit.service

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.itshixun.qstdevtoolkit.common.ServiceHelper
import com.itshixun.qstdevtoolkit.structure.RestServiceProject

@Service(Service.Level.PROJECT)
class ProjectInitService(private val project: Project): Disposable {
    val serviceProjects:MutableList<RestServiceProject> = ArrayList<RestServiceProject>()
    override fun dispose() {
        println("ProjectInitService dispose")
    }
    fun parseServiceProjects() {
        serviceProjects.clear()
         DumbService.getInstance(project).runReadActionInSmartMode {
             println("addAll projects")
             serviceProjects.addAll(ServiceHelper.buildRestServiceProject(project))
             println("serviceProjects:::: ${serviceProjects.size}")
             println("======getServiceProjectsEnd=====")
         }
    }

}