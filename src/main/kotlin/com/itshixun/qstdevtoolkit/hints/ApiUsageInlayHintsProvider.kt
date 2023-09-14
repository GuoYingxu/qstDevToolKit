package com.itshixun.qstdevtoolkit.hints

import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.*
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class ApiUsageInlayHintsProvider:InlayHintsProvider<ApiUsageInlayHintsProvider.Settings> {
    data class Settings(var showApiUsage:Boolean = true)

    companion object {
        val ourkey: SettingsKey<Settings> = SettingsKey("QstToolKit.apiUsage.hints")
    }

    override val key: SettingsKey<Settings>
        get() = ourkey
    override val name: String
        get() = "QstToolKit.apiUsage.hints"
    override val previewText: String?
        get() = null

    override fun createSettings(): Settings = Settings()
    override val group: InlayGroup
        get() = InlayGroup.CODE_AUTHOR_GROUP
    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): InlayHintsCollector? {
        val project = file.project
        val document = PsiDocumentManager.getInstance(project).getDocument(file)
        val vFile = file.virtualFile
        var moduleName = ""
        ProjectFileIndex.getInstance(project).getModuleForFile(vFile)?.let {
               moduleName = it.name
        }

        return document?.let{
            ApiUsageHintsCollector(editor,document,moduleName)
        }

    }

    override fun createConfigurable(settings: Settings): ImmediateConfigurable = object:ImmediateConfigurable {
        override fun createComponent(listener: ChangeListener): JComponent = panel {  }

    }
}