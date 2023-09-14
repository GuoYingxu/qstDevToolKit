package com.itshixun.qstdevtoolkit.structure

import com.intellij.ide.DefaultTreeExpander
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.tree.AsyncTreeModel
import com.intellij.ui.tree.StructureTreeModel
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.ui.treeStructure.SimpleTreeStructure
import com.itshixun.qstdevtoolkit.service.ProjectInitService

/**
 * 构造树结构
 */
class ApiTreeStructure(private val project: Project, tree: SimpleTree): SimpleTreeStructure() {
    private val structureTreeModel = StructureTreeModel(this, project)
    private val asyncTreeModel = AsyncTreeModel(structureTreeModel, project)
    var serviceCount = 0
    private val rootNode = RootNode()
    private val projectToNodeMap = HashMap<RestServiceProject,ProjectNode>()
    init {
        tree.showsRootHandles = true
        tree.isRootVisible = true
        tree.model = asyncTreeModel
        val treeExpander = DefaultTreeExpander(tree)
        treeExpander.expandAll()
    }
//    fun update(){
//        update(false)
//    }
    fun updateProject(moduleName:String) {
        val projects = project.service<ProjectInitService>().serviceProjects
        for(restProject in  projects) {
            if(restProject.moduleName == moduleName) {
                println("====restProject:::: ${moduleName}")
                restProject.update()
//                structureTreeModel.invalidateAsync()
                rootNode.childrenChanged()
                rootNode.updateProjectNodes(projects)
            }
        }
    }
    fun update(needRefresh:Boolean) {
        println("ApiTreeStructure::update")
//        if(needRefresh) {
//            println("refresh======projects")
            project.service<ProjectInitService>().parseServiceProjects()
//        }
        val projects = project.service<ProjectInitService>().serviceProjects
            updateProjects(projects)
        if(needRefresh) {
            structureTreeModel.invalidateAsync()
        }
    }
    private fun updateProjects(projects:List<RestServiceProject>) {
        serviceCount = 0
        println("set count = 0,projects::::${projects.size}")
        for (project1 in projects) {
            var node = findNodeFor(project1)
            if (node == null) {
                node = ProjectNode(rootNode, project1)
                projectToNodeMap[project1] = node
            }
            serviceCount += project1.serviceItems.size
        }
        rootNode.childrenChanged()
        rootNode.updateProjectNodes(projects)
        println("=======serviceCount$serviceCount")
    }
    private  fun findNodeFor(rsProject:RestServiceProject):ProjectNode? {
        return projectToNodeMap[rsProject]
    }
    override fun getRootElement():RootNode = rootNode

    inner class RootNode(): BaseSimpleNode(null) {
        private var projectNodes:ArrayList<ProjectNode>  = ArrayList()
        override fun buildChildren(): Array<SimpleNode>  = projectNodes.toTypedArray()
        override fun getName(): String {
            println("-------getRootNodeName")
            // TODO bug rootName  不更新
//            "Found $serviceCount services"

            // in {controllerCount} Controllers";
            val s = "Found %d services "
            return  String.format(s, serviceCount)
        }
        /** 更新节点 */
        fun updateProjectNodes(projects:List<RestServiceProject>) {
            projectNodes.clear()
            projects.forEach {
                projectNodes.add(ProjectNode(this,it))
            }
//            updateFrom(parent)
            childrenChanged()
//            updateUpTo(this)
        }

    }

    companion object {
        fun getSelectNodes(sTree: SimpleTree,clazz:Class<BaseSimpleNode>): List<BaseSimpleNode> {
            return getSelectedNodes(sTree).filter { clazz.isInstance(it) }.map { it as BaseSimpleNode }
        }

        private fun getSelectedNodes(sTree: SimpleTree): List<SimpleNode> {
           return sTree.selectionPaths?.mapNotNull {
               sTree.getNodeFor(it)
           } ?: emptyList()
        }
    }
}

