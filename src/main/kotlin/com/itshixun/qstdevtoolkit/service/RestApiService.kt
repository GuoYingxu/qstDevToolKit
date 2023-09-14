package com.itshixun.qstdevtoolkit.service

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ex.ToolWindowManagerEx
import com.intellij.ui.treeStructure.SimpleTree
import com.itshixun.qstdevtoolkit.structure.ApiTreeStructure
import com.itshixun.qstdevtoolkit.utils.ToolKitUtil
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JLabel
import javax.swing.tree.TreeSelectionModel

/**
 * api service
 *  管理toolwindow
 *  检索 api 接口
 */
@Service(Service.Level.PROJECT)
class RestApiService(private val project: Project){
    var apiTree:SimpleTree? = null
    private var apiTreeStructure:ApiTreeStructure? = null
    private var apiServiceWindow:ToolWindow? = null
    companion object{
        const val TOOLWINDOW_ID = "RestApiService"
    }
    init{
        apiServiceWindow = ToolWindowManagerEx.getInstanceEx(project).getToolWindow(TOOLWINDOW_ID)
        initTree()
    }


    /**
     * 初始化tree
     */
    fun initTree() {
        println("initTree")
        apiTree = object : SimpleTree() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val myLabel = JLabel(
//                    RestfulToolkitBundle.message(
                    "toolkit.navigator.nothing.to.display",
//                        ToolkitUtil.formatHtmlImage(jiux.net.plugin.restful.navigator.RestServicesNavigator.SYNC_ICON_URL)
//                    )
                )
                if (project.isInitialized) {
                    return
                }
                myLabel.font = font
                myLabel.background = background
                myLabel.foreground = foreground
                val bounds = bounds
                val size: Dimension = myLabel.preferredSize
                myLabel.setBounds(0, 0, size.width, size.height)
                val x = (bounds.width - size.width) / 2
                val g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height)
                try {
                    myLabel.paint(g2)
                } finally {
                    g2.dispose()
                }
            }
        }
        apiTree!!.emptyText.clear()
        apiTree!!.selectionModel.selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
    }

    /**
     * 更新 数据
     */
//    fun structureUpdate() {
//        println("RestApiService structureUpdate")
//        structureUpdate(false)
//
//    }
    fun structureUpdate(needRefresh:Boolean) {
        structurRequest( Runnable{
            apiTreeStructure!!.update(needRefresh)
        })

    }
    fun structureUpdateModule(moduleName:String) {
        if(apiTreeStructure == null) {
            return
        }
        Runnable {
            apiTreeStructure!!.updateProject(moduleName)
        }


    }
    /**
     * 单独的线程 更新structure
     */
    private fun structurRequest(r:Runnable) {
        println("RestApiService structurRequest")
        ToolKitUtil.runWhenProjectIsReady(project, Runnable {
            if (project.isDisposed) {
                return@Runnable
            }
//            apiServiceWindow!!.isVisible.let {
//                if (!it) {
//                    return@Runnable
//                }
//            }
            if (apiTreeStructure == null) {
                println(" 初始化 apiTreeStructure")
                apiTreeStructure = ApiTreeStructure(project, apiTree!!)
            }
            r.run()
//            apiTreeStructure!!.update()
        })
    }



}