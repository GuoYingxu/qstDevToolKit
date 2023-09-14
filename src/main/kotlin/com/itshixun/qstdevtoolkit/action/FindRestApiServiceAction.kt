package com.itshixun.qstdevtoolkit.action

import com.intellij.ide.actions.GotoActionBase
import com.intellij.ide.util.gotoByName.ChooseByNameFilter
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider
import com.intellij.ide.util.gotoByName.ChooseByNameModel
import com.intellij.ide.util.gotoByName.ChooseByNamePopup
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.itshixun.qstdevtoolkit.configuration.RestApiServiceFinderConfiguration
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import com.itshixun.qstdevtoolkit.structure.method.HttpMethod
import org.jetbrains.annotations.Nls
import javax.swing.Icon

open class FindRestApiServiceAction:GotoActionBase(),DumbAware{
    override fun gotoActionPerformed(e: AnActionEvent) {
        if(e.project == null){
            return
        }
        e.project?.let {
            val module = e.getData(PlatformDataKeys.MODULE)

            val chooseByNameContributors = arrayListOf<ChooseByNameContributor>( RestApiServiceFinderContributor(module))
            val model = RestApiServiceFinderModel(it, chooseByNameContributors)
            val callback: GotoActionCallback<HttpMethod> = object : GotoActionCallback<HttpMethod>() {

                override fun createFilter(popup: ChooseByNamePopup): ChooseByNameFilter<HttpMethod> {

                    val configuration = RestApiServiceFinderConfiguration.getInstance(e.project)

                  return GotoRequestMappingFilter(popup, model, configuration!!, e.project!!)
                }

                override fun elementChosen(chooseByNamePopup: ChooseByNamePopup, element: Any) {
                    if (element is RestServiceItem) {
                        val navigationItem: RestServiceItem = element
                        if (navigationItem.canNavigate()) {
                            navigationItem.navigate(true)
                        }
                    }
                }
            }
            val psiElement: PsiElement = getPsiContext(e.project) ?: return
            val provider = RestApiServiceFinderProvider(psiElement)

            val start = getInitialText(true, e)
            val predefinedText = start.first
            super.showNavigationPopup(callback, "Find RestApi Service",
                RestApiServiceFinderPopup.createPopup(e.project,
                    model,
                    provider,
                    predefinedText,
                    model.willOpenEditor() && FileEditorManagerEx.getInstanceEx(e.project!!).hasSplitOrUndockedWindows(),
                    start.second)
                , true)

//            this.showNavigationPopup(e,
//                model,
//                callback,
//                "Find RestApi Service",
//                true,
//                true, provider)
        }
    }



    /**
     * 弹出搜索面板
     */
    override fun <T : Any?> showNavigationPopup(
        anActionEvent: AnActionEvent,
        model: ChooseByNameModel?,
        callback: GotoActionCallback<T>?,
        @Nls findUsagesTitle: @Nls String?,
        useSelectionFromEditor: Boolean,
        allowMultipleSelection: Boolean,
        itemProvider: ChooseByNameItemProvider?
    ) {
        // 从当前编辑器中获取选中的内容
        val start = getInitialText(useSelectionFromEditor, anActionEvent)
        val predefinedText = start.first
        super.showNavigationPopup(callback, findUsagesTitle,
            RestApiServiceFinderPopup.createPopup(anActionEvent.project,
                model!!,
                itemProvider!!,
                predefinedText,
                model.willOpenEditor() && FileEditorManagerEx.getInstanceEx(anActionEvent.project!!).hasSplitOrUndockedWindows(),
                start.second)
            , allowMultipleSelection)
    }



    inner class GotoRequestMappingFilter(
        popup: ChooseByNamePopup,
        model: RestApiServiceFinderModel,
        configuration: RestApiServiceFinderConfiguration,
        project: Project
    ) :
        ChooseByNameFilter<HttpMethod>(popup, model, configuration, project){
//        constructor(popup: ChooseByNamePopup, model: RestApiServiceFinderModel, project: Project) : this(
//            popup,
//            model,
//            RestApiServiceFinderConfiguration.getInstance(project)!!,
//            project
//        )
        override fun getAllFilterValues(): List<HttpMethod> {
            return listOf(*HttpMethod.values())
        }

        override fun textForFilterValue(value: HttpMethod): String {
            return value.name
        }

        override fun iconForFilterValue(value: HttpMethod): Icon? {
            return null
        }
    }

}