package com.itshixun.qstdevtoolkit.window

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.JBColor
import com.intellij.ui.PopupHandler
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.SimpleTree
import com.itshixun.qstdevtoolkit.service.RestApiService
import com.itshixun.qstdevtoolkit.structure.*
import com.itshixun.qstdevtoolkit.utils.RestApiServiceDataKey
import java.awt.Component
import javax.swing.BorderFactory

class RestApiServicePanel(project: Project): SimpleToolWindowPanel(true,true),DataProvider {
    private var tree:SimpleTree? = null
    init {
        // add menus
        // 添加toolbar 菜单
        ActionManager.getInstance().createActionToolbar(
            "RestApiServicePanel toolbar",
            ActionManager.getInstance().getAction("qstDevToolkit.RestApiServiceToolbar") as DefaultActionGroup,
            true)
            .apply {
                targetComponent = this@RestApiServicePanel
                toolbar = component
            }

        // add tree
        tree = project.service<RestApiService>().apiTree

        val scrollPane = ScrollPaneFactory.createScrollPane(tree)
        scrollPane.border = BorderFactory.createLineBorder(JBColor.RED)
        setContent(scrollPane)
        tree!!.addMouseListener(object: PopupHandler() {
            override fun invokePopup(comp: Component?, x: Int, y: Int) {
                val menuId = getMenuId(ApiTreeStructure.getSelectNodes(tree!!, BaseSimpleNode::class.java))
                if(menuId != null) {
                    val actionGroup = ActionManager.getInstance().getAction(menuId) as ActionGroup
                   ActionManager.getInstance().createActionPopupMenu("popup", actionGroup).component.show(comp, x, y)
                }
            }
            fun getMenuId(nodes:List<BaseSimpleNode>):String? {
                for(node in nodes) {
                    val menuId = node.getMenuId()
                    if(menuId != null) {
                        return menuId
                    }
                }
                return null
            }
        })
    }

    private fun getSelectedNodes(aClass: Class<BaseSimpleNode>): List<BaseSimpleNode> {
        if(tree == null) {
            return emptyList()
        }
        return ApiTreeStructure.getSelectNodes(tree!!, aClass)
    }
    override fun getData(dataId: String): Any? {
        if(RestApiServiceDataKey.SERVICE_ITEMS.`is`(dataId)) {
            return extractServices()
        }
        if(RestApiServiceDataKey.SERVICE_MODULE.`is`(dataId)) {
            return extractModules()
        }
        return super.getData(dataId)
    }

    /**
     * 选中的 module 节点
     */
    private fun extractModules():List<ProjectNode> {
        val result: MutableList<ProjectNode> = ArrayList()
        val selectNodes:Collection<BaseSimpleNode> = getSelectedNodes(BaseSimpleNode::class.java)
        for( selectedNode  in selectNodes) {
            if(selectedNode is ProjectNode) {
                result.add(selectedNode )
            }
        }
        return result
    }

    /**
     * 获取选中的节点
     */
    private fun extractServices(): List<RestServiceItem> {
        val result: MutableList<RestServiceItem> = ArrayList()
        val selectedNodes: Collection<BaseSimpleNode> = getSelectedNodes(BaseSimpleNode::class.java)
        for (selectedNode in selectedNodes) {
            if (selectedNode is ApiServiceNode) {
                result.add(selectedNode.serviceItem)
            }
        }
        return result
    }
}