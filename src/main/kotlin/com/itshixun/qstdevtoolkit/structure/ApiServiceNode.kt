package com.itshixun.qstdevtoolkit.structure

import com.intellij.lang.java.JavaLanguage
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.OpenSourceUtil
import com.itshixun.qstdevtoolkit.utils.ToolKitIconsUtil
import java.awt.event.InputEvent

class ApiServiceNode(aParent:SimpleNode?, val serviceItem:RestServiceItem):BaseSimpleNode(aParent){
    init {
        val icon = ToolKitIconsUtil.getIcon(serviceItem.method)
        templatePresentation.setIcon(icon)
    }

    /**
     * apiServiceNode 是叶子节点，没有子节点
     */
    override fun buildChildren(): Array<SimpleNode> {
        return emptyArray()
    }

    override fun getName(): String = serviceItem.name

    override fun getMenuId():String {
        return "qstDevToolkit.RestApiServiceMenu"
    }
    /**
     * 双击跳转
     */
    override fun handleDoubleClickOrEnter(tree: SimpleTree?, inputEvent: InputEvent?) {
        try {
            tree?.let {
                val currentNode = it.selectedNode as ApiServiceNode
                val serviceItem = currentNode.serviceItem
                val psiElement = serviceItem.psiElement
                if(psiElement.isValid && psiElement.language == JavaLanguage.INSTANCE) {
                    OpenSourceUtil.navigate(serviceItem.psiMethod)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}