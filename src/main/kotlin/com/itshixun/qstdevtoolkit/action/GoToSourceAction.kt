package com.itshixun.qstdevtoolkit.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.itshixun.qstdevtoolkit.utils.RestApiServiceDataKey

class GoToSourceAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val serviceItems = RestApiServiceDataKey.SERVICE_ITEMS.getData(e.dataContext)
        if(serviceItems!= null) {
            for (item in serviceItems) {
                if (item.canNavigate()) {
                    item.navigate(true)
                }
            }
        }
    }
}