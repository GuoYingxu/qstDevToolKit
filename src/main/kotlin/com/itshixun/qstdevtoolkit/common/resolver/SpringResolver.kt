package com.itshixun.qstdevtoolkit.common.resolver

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifierList
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex
import com.intellij.psi.search.GlobalSearchScope
import com.itshixun.qstdevtoolkit.common.ServiceHelper
import com.itshixun.qstdevtoolkit.common.annotations.SpringControllerAnnotation
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import com.itshixun.qstdevtoolkit.utils.RestApiCache

class SpringResolver:BaseServiceResolver{
    constructor(project: Project):super() {
        this.project = project
    }
    constructor(module: Module):super(){
        this.module = module
    }
    /**
     * find all supported service items in project
     * 解析 project 下的所有 Spring Controller
     * @param project 项目
     * @param globalSearchScope 搜索范围
     * @return List<RestServiceItem>
     */
    override fun getRestServiceItems(project: Project, globalSearchScope: GlobalSearchScope): List<RestServiceItem> {
//        println("SpringResolver::getRestServiceItems")
        val itemList:ArrayList<RestServiceItem> = ArrayList()
        // java (Rest)Controller 枚举
        val supportedAnnotations = SpringControllerAnnotation.values()
//        println("supportedAnnotations:::::: ${supportedAnnotations.size}")
        supportedAnnotations.forEach { it ->
//            println(it.getShortName())
//            println(project.name)
//            println(JavaAnnotationIndex.getInstance().getAllKeys(project).size)
            // 匹配的controller注解
            val psiAnnotations = JavaAnnotationIndex.getInstance().get( it.getShortName(),project,globalSearchScope)
            println("javaAnnotationIndex:::::${JavaAnnotationIndex.getInstance().getAllKeys(project).size}")
            println("psiAnnotations:::::: ${psiAnnotations.size}")
            if(psiAnnotations.isNullOrEmpty()) {
                return@forEach
            }
            psiAnnotations.forEach { pisAnnotation ->
                run {
                    val modifyList = pisAnnotation.parent as PsiModifierList
                    // controller 持有类
                    val psiClass = modifyList.parent as PsiClass
                     ProjectFileIndex.getInstance(project).getModuleForFile(psiClass.containingFile.virtualFile)?.let {
                        itemList.addAll(getRestServiceItemList(psiClass,it))
                    }
                }
            }

        }
        return itemList
    }

    /**
     * 解析 controller 下的所有 method
     *
     * @param psiClass controller 类
     * @return List<RestServiceItem>
     *
     */
    private fun getRestServiceItemList(psiClass: PsiClass,module:Module):List<RestServiceItem> {
        val itemList:ArrayList<RestServiceItem> = ArrayList()
        val psiMethods = psiClass.methods
        // class 的 url method
        val classMappingPaths = ServiceHelper.getRequestPaths(psiClass)
        if(classMappingPaths.isEmpty()) return itemList
        psiMethods.forEach {
            // 方法的 url method
            val methodMappingPaths = ServiceHelper.getRequestPaths(it)
            for (classMappingPath in classMappingPaths) {
                for(methodMappingPath in methodMappingPaths) {
                    // 构造 RestServiceItem 对象
                    val classPath = classMappingPath.path
                    val restServiceItem = createRestServiceItem(it,classPath,methodMappingPath,module)
                    itemList.add(restServiceItem)
                    // 记录
                    restServiceItem.path?.let {
                        RestApiCache.set(restServiceItem.path!!,restServiceItem)
                    }
                }

            }
        }

        return itemList
    }
}