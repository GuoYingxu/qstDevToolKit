package com.itshixun.qstdevtoolkit.window

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class RestApiServiceWindowFactory:ToolWindowFactory,DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        val panel = RestApiServicePanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel,"",false)
        toolWindow.contentManager.addContent(content)
        println("--------creatwindowContent")
    }

    override fun isApplicable(project: Project): Boolean {
        return DumbService.isDumb(project)
    }

}