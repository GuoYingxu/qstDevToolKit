package com.itshixun.qstdevtoolkit.structure

import com.intellij.ui.treeStructure.CachingSimpleNode
import com.intellij.ui.treeStructure.SimpleNode

abstract  class BaseSimpleNode(aParent: SimpleNode?) : CachingSimpleNode(aParent) {
    /**
     * children 发生变化，清除 cache，
     * 同时清除父节点的cache
     */
    fun childrenChanged() {
        var each:BaseSimpleNode = this
        while (true) {
            each.cleanUpCache()
            each = each.parent as? BaseSimpleNode ?: break
        }
    }
    open fun getMenuId():String? {
        return null
    }

}