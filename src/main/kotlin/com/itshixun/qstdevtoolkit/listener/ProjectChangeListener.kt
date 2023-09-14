package com.itshixun.qstdevtoolkit.listener

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.itshixun.qstdevtoolkit.service.RestApiService
import com.itshixun.qstdevtoolkit.utils.ToolKitUtil

/**
 * 项目打开关闭监听
 * 初始化 toolwindow
 *    当项目打开时，扫描项目中的所有接口，生成接口列表
 *
 */
class ProjectChangeListener: ProjectManagerListener {
    /**
     * 项目打开时
     * 初始化toolwindow,扫描项目中的所有接口，生成接口列表
     * TODO 使用新的接口处理 初始化项目
     */
    override fun projectOpened(project: Project) {
        if(ApplicationManager.getApplication().isUnitTestMode) return
        // todo 初始化toolwindow
        println("projectOpened")
        ToolKitUtil.runWhenInitialized(project) {
            println("projectInitialized")
            if(!project.isDisposed) {
                project.service<RestApiService>().structureUpdate(true);
            }
        }
    }

}