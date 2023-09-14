package com.itshixun.qstdevtoolkit.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.itshixun.qstdevtoolkit.service.RestApiService

class RefreshRestApiServiceAction:AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.service<RestApiService>()?.structureUpdate(true)
    }
}