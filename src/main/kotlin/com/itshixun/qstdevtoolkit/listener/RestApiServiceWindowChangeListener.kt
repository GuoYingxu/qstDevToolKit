package com.itshixun.qstdevtoolkit.listener

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.itshixun.qstdevtoolkit.service.HwApiService
import com.itshixun.qstdevtoolkit.service.RestApiService

class RestApiServiceWindowChangeListener(private val project: Project):ToolWindowManagerListener {
    override fun toolWindowShown(toolWindow: ToolWindow) {
        if(toolWindow.id == RestApiService.TOOLWINDOW_ID) {
            project.service<RestApiService>().structureUpdate(false)
        }
        println("${toolWindow.id} :${HwApiService.TOOLWINDOW_ID}")
        if(toolWindow.id == HwApiService.TOOLWINDOW_ID){

            project.service<HwApiService>().updateProjects(project,false)
        }
    }
}