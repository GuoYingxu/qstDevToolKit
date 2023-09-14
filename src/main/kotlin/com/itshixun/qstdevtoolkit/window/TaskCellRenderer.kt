package com.itshixun.qstdevtoolkit.window

import com.huaweicloud.sdk.projectman.v4.model.ListIssueItemResponse
import com.intellij.icons.AllIcons
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class TaskCellRenderer: JLabel(), ListCellRenderer<Any> {
    override fun getListCellRendererComponent(list: JList<out Any>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val data = value as ListIssueItemResponse
        if ( data.tracker != null && data.tracker.name.equals("Task")) {
            icon = AllIcons.Actions.Annotate
        } else {
            icon = AllIcons.Actions.StartDebugger
        }
        text = data.name
//            preferredSize = Dimension(280, 28)
        return this
    }
}