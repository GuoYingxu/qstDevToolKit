package com.itshixun.qstdevtoolkit.structure

import com.intellij.ui.treeStructure.SimpleNode
import com.itshixun.qstdevtoolkit.utils.ToolKitIconsUtil

class ProjectNode (aParent:BaseSimpleNode, val project: RestServiceProject):BaseSimpleNode(aParent){
    private var apiServiceNodes = ArrayList<ApiServiceNode>()
    init {
        templatePresentation.setIcon(ToolKitIconsUtil.MOUDLE_ICON)
        updateServiceNodes(project.serviceItems)
    }
    /** 更新节点 */
    private fun updateServiceNodes(serviceItems:List<RestServiceItem>) {
        apiServiceNodes.clear()
        serviceItems.forEach {
            println(it.url)
            apiServiceNodes.add(ApiServiceNode(this,it))
        }
        (parent as BaseSimpleNode).cleanUpCache()
    }
    override fun getName(): String {
        return if (project.appName == project.moduleName) {
            project.moduleName
        }else {
            "${project.moduleName} (${project.appName})"
        }
    }
//        .replace(".main","").split(".").last()


    override fun buildChildren(): Array<SimpleNode> = apiServiceNodes.toTypedArray()

    /**
     * action group id
     */
    override fun getMenuId():String {
        return "qstDevToolkit.RestApiProjectMenu"
    }


}