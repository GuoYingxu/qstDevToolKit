package com.itshixun.qstdevtoolkit.action

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider
import com.intellij.ide.util.gotoByName.ChooseByNameModel
import com.intellij.ide.util.gotoByName.ChooseByNamePopup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil

/**
 *  搜索 弹窗
 */
class RestApiServiceFinderPopup(project: Project?, model: ChooseByNameModel, provider: ChooseByNameItemProvider,
                                oldPopup: ChooseByNamePopup?,predefinedText:String?,mayRequestOpenInCurrentWindow:Boolean,initalIndex:Int
) :ChooseByNamePopup(project, model,
    provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initalIndex
) {

    companion object {
        private val CHOOSE_API_BY_NAME_POPUP_IN_PROJECT_KEY:Key<RestApiServiceFinderPopup> = Key.create("ChooseApiByNamePopup")
        fun createPopup(project: Project?,
                        model: ChooseByNameModel,
                        provider: ChooseByNameItemProvider,
                        predefinedText:String?,
                        mayRequestOpenInCurrentWindow:Boolean,
                        initalIndex:Int):RestApiServiceFinderPopup {
            // 如果输入内容为空 则直接返回
            if(StringUtil.isEmptyOrSpaces(predefinedText)) {
                return RestApiServiceFinderPopup(project, model, provider, null, predefinedText, mayRequestOpenInCurrentWindow, initalIndex)
            }
            // 关闭旧的弹窗
            val oldPopup = project?.getUserData(CHOOSE_API_BY_NAME_POPUP_IN_PROJECT_KEY)
            oldPopup?.close(false)
            val newPopup = RestApiServiceFinderPopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initalIndex)

            project?.putUserData(CHOOSE_API_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup)
            return newPopup
        }
    }

}