package com.itshixun.qstdevtoolkit.common.resolver

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.itshixun.qstdevtoolkit.structure.RequestPath
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import org.jetbrains.annotations.NotNull

abstract class BaseServiceResolver:ServiceResolver {
    var module:Module? = null
    var project: Project? = null
    override fun findAllSupportedServiceItemsInModule(): List<RestServiceItem> {
        if(module == null) {
            println("findAllSupportedServiceItemsInModule::: module is null")
            return emptyList()
        }
        val globalSearchScope = GlobalSearchScope.moduleScope(module!!)
        return getRestServiceItems( module!!.project, globalSearchScope)
    }

    override fun findAllSupportedServiceItemsInProject(): List<RestServiceItem> {
        project = project?:module?.project
        project?.let {
            val globalSearchScope = GlobalSearchScope.projectScope(it)
            return getRestServiceItems(it,globalSearchScope)
        }
        return emptyList()
    }

    /**
     *  构造 RestServiceItem 对象
     *  @param psiMethod 方法
     *  @param classUriPath controller类的 mapping url
     *  @param requestMapping {RequestPath} 方法的 mapping url和method
     *  @return {RestServiceItem}
     */
    @NotNull
    protected fun  createRestServiceItem(psiMethod: PsiElement, classUriPath:String, requestMapping: RequestPath,module:Module):RestServiceItem {
        val path = if(classUriPath.isBlank()) {
            requestMapping.path.replace(Regex("\\/\$"),"")
        }else {
            if(requestMapping.path.isBlank()) {
                classUriPath.replace(Regex("\\/\$"),"")
            }else {
                "${classUriPath.replace(Regex("\\/\$"),"")}/${requestMapping.path.replace(Regex("^/"),"")}"
            }
        }
        return RestServiceItem(psiMethod, requestMapping.method, path,module)
    }
    /** should be overridden by subclass */
    abstract fun getRestServiceItems( project:Project,globalSearchScope: GlobalSearchScope):List<RestServiceItem>

}