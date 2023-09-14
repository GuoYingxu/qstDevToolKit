package com.itshixun.qstdevtoolkit.window

import com.huaweicloud.sdk.projectman.v4.model.ListIssueItemResponse
import com.huaweicloud.sdk.projectman.v4.model.ListProjectsV4ResponseBodyProjects
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.itshixun.qstdevtoolkit.service.HwApiService
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*

class TaskListWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val  service = project.service<HwApiService>()
        val projectListModel = service.projectsComboxModel
        val comboBox = ComboBox(projectListModel)
        if(projectListModel.size>0) {
            comboBox.item = projectListModel.getElementAt(0)
        }
        comboBox.renderer = ProjectListCellRender()
        comboBox.addItemListener() {
            println("selectedItem")
            service.updateTasks(project, it.item as ListProjectsV4ResponseBodyProjects)
        }


        val taskListPanel = createPanel(project, comboBox)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(taskListPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    fun createPanel(project: Project, jcombox: ComboBox<Any>): JPanel {
        val taskList = JBList(project.service<HwApiService>().taskListModel)
        taskList.cellRenderer = TaskCellRenderer() as ListCellRenderer<in Any?>
        taskList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                if (taskList.selectedIndex != -1) {
//                    if (event.clickCount == 1) {
//                        //单击
//                    }
                    if (event.clickCount == 2) {
                        //双击
                        try {
                            val hwProject = jcombox.item as ListProjectsV4ResponseBodyProjects
                            hwProject?.projectId.let {
                                val task: ListIssueItemResponse = taskList.selectedValue as ListIssueItemResponse
                                val url = "https://devcloud.huaweicloud.com/projectman/scrum/$it/task/detail/${task.id}"
                                //                            Runtime.getRuntime().exec("cmd.exe /c start " + url);
                                val desktop = Desktop.getDesktop()
                                desktop.browse(URI(url))
                            }
                        } catch (e: Exception) {
                            System.out.printf("error" + e.message)
                        }
                    }
                }
            }
        })
        val rootPanel = JPanel(BorderLayout())
        val projectPanel =
                panel {
                    row {
                        label("项目列表").horizontalAlign(HorizontalAlign.LEFT)
                        cell(jcombox).horizontalAlign(HorizontalAlign.FILL)
                        button("刷新") {

                            if (jcombox.item == null) {
                                if (jcombox.model.size > 0) {
                                    jcombox.item = jcombox.model.getElementAt(0)
                                } else {
                                    return@button
                                }
                            }
                            val hwProject = jcombox.item as ListProjectsV4ResponseBodyProjects
                            hwProject.let {
                                project.service<HwApiService>().updateTasks(project, hwProject)
                            }
                            return@button
                        }.horizontalAlign(HorizontalAlign.RIGHT)
                    }
                }
        val scrollPanel = JBScrollPane(taskList)
        rootPanel.add(projectPanel,BorderLayout.NORTH)
        rootPanel.add(scrollPanel,BorderLayout.CENTER)
        return rootPanel
    }

    inner class ProjectListCellRender:JLabel(), ListCellRenderer<Any> {
        override fun getListCellRendererComponent(
                list: JList<out Any>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
        ): Component {
            if (value is ListProjectsV4ResponseBodyProjects) {
                text = value.projectName
            }
            return this
        }

    }
}