package com.itshixun.qstdevtoolkit.action

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.gotoByName.CustomMatcherModel
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.NameUtil
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import com.itshixun.qstdevtoolkit.structure.method.HttpMethod

/**
 * model for "go to | File" action
 */
class RestApiServiceFinderModel(private val project: Project, contributors:ArrayList<ChooseByNameContributor>):
    FilteringGotoByModel<HttpMethod>(project,contributors),DumbAware,CustomMatcherModel {
    /**
     * prompt 提示语
     */
    override fun getPromptText(): String = "Enter Service path"
    /**
     * not in message
     */
    override fun getNotInMessage(): String {
        return IdeBundle.message("label.no.matches.found",project.name)
    }

    /**
     * not found message
     */
    override fun getNotFoundMessage(): String {
        return IdeBundle.message("label.no.matches.found")
    }

    override fun getCheckBoxName(): String  = buildString {
        append("In This Module")
    }

    override fun loadInitialCheckBoxState(): Boolean =
        PropertiesComponent.getInstance(project).isTrueValue("RestApiServiceFinder.InThisModule")

    override fun saveInitialCheckBoxState(state: Boolean) {
        with(PropertiesComponent.getInstance(project)) {
           if( isTrueValue("RestApiServiceFinder.InThisModule") ){
                setValue("RestApiServiceFinder.InThisModule", state.toString())
            }
        }
    }

    override fun getSeparators(): Array<String> = arrayOf("?","/")

    override fun getFullName(element: Any): String? = getElementName(element)

    override fun willOpenEditor(): Boolean  = true

    override fun filterValueFor(item: NavigationItem?): HttpMethod? {
        if(item is RestServiceItem){
            return item.method
        }
        return null
    }

    override fun matches(popupItem: String, userPattern: String): Boolean {
        if (userPattern == "/") return true

        // REST style params:  @RequestMapping(value="{departmentId}/employees/{employeeId}")  PathVariable
        val matcher = NameUtil.buildMatcher("*$userPattern", NameUtil.MatchingCaseSensitivity.NONE)
        //        if(!matched) {
//            // REST style params(regex) @RequestMapping(value="/{textualPart:[a-z-]+}.{numericPart:[\\d]+}")  PathVariable
//            matched = AntPathMatcher().match(popupItem,userPattern)
//        }
        return matcher.matches(popupItem)
    }


}