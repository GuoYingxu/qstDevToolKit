package com.itshixun.qstdevtoolkit.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.dsl.builder.panel
import com.itshixun.qstdevtoolkit.service.RestApiModuleSettingState
import com.itshixun.qstdevtoolkit.service.RestApiService
import com.itshixun.qstdevtoolkit.structure.ProjectNode
import com.itshixun.qstdevtoolkit.utils.RestApiServiceDataKey
import java.awt.BorderLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JPanel

/**
 * 配置 项目
 * 弹出配置面板
 */
class ProjectConfigAction:AnAction() {
    private var tmpPrefix:String? = null
    private var tmpAppName:String? = null
    override fun actionPerformed(e: AnActionEvent) {
        val moduleNodes = RestApiServiceDataKey.SERVICE_MODULE.getData(e.dataContext)
        if (moduleNodes.isNullOrEmpty()) {
            return
        }
        val node = moduleNodes[0]
        if(node is ProjectNode) {
            popUpSettings(node.project.moduleName,e.project!!)
        }
    }
    private fun popUpSettings(moduleName:String, project:Project) {
        val configPanel = panel {
            row("Module:") {
                label(moduleName)
            }
            row("服务名：") {
                textField().applyToComponent {
                    tmpAppName = RestApiModuleSettingState.getServerName(moduleName, project)
                    this.text = tmpAppName
                    val textField = this
                    this.addFocusListener(
                        object: FocusListener {
                            override fun focusGained(e: FocusEvent?) { }
                            override fun focusLost(e: FocusEvent?) {
                                    tmpAppName = textField.text
                            }
                        }
                    )
                }
            }
            row("apiPrefix:") {
                textField().applyToComponent {
                    tmpPrefix = RestApiModuleSettingState.getPrefix(moduleName,project)
                    val textField = this
                    this.text = tmpPrefix
                    this.addFocusListener(
                        object: FocusListener {
                            override fun focusGained(e: FocusEvent?) {
                            }
                            override fun focusLost(e: FocusEvent?) {
                                tmpPrefix = textField.text
                            }

                        }
                    )
                }
            }
        }

        val panel = JPanel(BorderLayout())

           panel.add(configPanel, BorderLayout.CENTER)
//            popupFactory.createComponentPopupBuilder(panel,configPanel)
//                .setTitle("模块设置")
//                .setMovable(true)
//                .setResizable(true)
//                .createPopup()
//                .showCenteredInCurrentWindow(project)

        val dialog =  DialogBuilder(project).centerPanel(panel)
        dialog.setTitle("模块设置")
        dialog.setOkOperation(
            Runnable {
                tmpAppName?.let {  RestApiModuleSettingState.updateServerName(moduleName, project,tmpAppName!!) }
                tmpPrefix?.let{ RestApiModuleSettingState.updatePrefix(moduleName,project,tmpPrefix!!)}
                project.service<RestApiService>().structureUpdateModule(moduleName)
                dialog.dialogWrapper.close(0)
            })
        dialog.show()
    }

}