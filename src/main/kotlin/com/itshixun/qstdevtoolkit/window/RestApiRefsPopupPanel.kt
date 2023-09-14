package com.itshixun.qstdevtoolkit.window

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.itshixun.qstdevtoolkit.service.ApiService.Companion.apiUsages
import com.itshixun.qstdevtoolkit.service.ApiUsageModel
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import java.awt.Component
import javax.swing.*

class RestApiRefsPopupFactory {
    companion object {
        fun createPanel(restServiceItem:RestServiceItem): JPanel {
            apiUsages.clear()
            if(restServiceItem.marker.refs == null || restServiceItem.marker.refs!!.isEmpty()) {
                apiUsages.addElement(ApiUsageModel("没找到引用","","",""))
            }else {
                restServiceItem.marker.refs!!.forEach { ref ->
                    run {
                        ref.files.split(';').forEach {
                            val url = it.split(':')[0]
                            val position = it.split(':')[1]
                            val fileType = url.split('.')[1]
                            val usage = ApiUsageModel(ref.clientName, url, position, fileType)
                            apiUsages.addElement(usage)
                        }
                    }
                }
            }
            val rootPanel = JPanel()
            val usageList = JBList<ApiUsageModel?>(apiUsages)
            val scrollPane = JBScrollPane(usageList)
            rootPanel.add(scrollPane)
            usageList.cellRenderer = ApiUsageCellRender()
            return rootPanel
        }
    }
}
internal class ApiUsageCellRender : JLabel(), ListCellRenderer<ApiUsageModel?> {
    override fun getListCellRendererComponent(
        list: JList<out ApiUsageModel?>?,
        value: ApiUsageModel?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        try {
            if(value!!.url.isEmpty()) {
               text = "没找到引用"
                return this
            }
            this.icon = AllIcons.FileTypes.JavaScript
            text = "#${value.clientName}#   ${value.url}:${value.position}"
        } catch (e: Exception) {
            e.printStackTrace()
            this.icon = null
            text = "error"
        }
        println("getListCellRendererComponent" + text + " " + this.icon)
        return this
    }

}