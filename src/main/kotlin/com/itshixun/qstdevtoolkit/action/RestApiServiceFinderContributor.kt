package com.itshixun.qstdevtoolkit.action

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.itshixun.qstdevtoolkit.common.ServiceHelper
import com.itshixun.qstdevtoolkit.structure.RestServiceItem

/**
 * Allows a plugin to add items to "Navigate Class|File|Symbol" lists.
 *
 */
class RestApiServiceFinderContributor(private val module:Module?):ChooseByNameContributor {
    private var navItems:List<RestServiceItem>? =null
    /**
     * Returns the names of the items found by the contributor.
     * @param project the project in which the search is performed.
     * @param includeNonProjectItems if true, items from outside the project are included in the list.
     * @return the array of names.
     */
    override fun getNames(project: Project, includeNonProjectItems: Boolean): Array<String> {
        if(includeNonProjectItems && module!= null) {
            navItems = ServiceHelper.buildRestApiServiceModule(module)
        }else {
            navItems = ServiceHelper.buildRestApiServiceProject(project)
        }
        return navItems!!.map { it.name }.toTypedArray()
    }

    /**
     * Returns the items matching the specified name.
     * @param name the name of the item to find.
     * @param pattern the pattern used for finding the item. For example, if the pattern is "J" then the items
     *                 whose names are "Java" and "JUnit" match the pattern.
     * @param project the project in which the search is performed.
     * @param includeNonProjectItems if true, items from outside the project are included in the list.
     * @return the array of items.
     */
    override fun getItemsByName(
        name: String?,
        pattern: String?,
        project: Project?,
        includeNonProjectItems: Boolean
    ): Array<NavigationItem> {
        if(navItems == null) {
            return emptyArray()
        }
        return navItems!!.filter { it.name == name }.toTypedArray()
    }

}