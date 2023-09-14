package com.itshixun.qstdevtoolkit.common

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.modules
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.itshixun.qstdevtoolkit.common.annotations.AnnotationHelper
import com.itshixun.qstdevtoolkit.common.annotations.SpringRequestMethodAnnotation
import com.itshixun.qstdevtoolkit.common.resolver.SpringResolver
import com.itshixun.qstdevtoolkit.service.RestApiModuleSettingState
import com.itshixun.qstdevtoolkit.structure.RequestPath
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import com.itshixun.qstdevtoolkit.structure.RestServiceProject

class ServiceHelper {

    companion object {
        /**
         * resolve RestServiceProject in project
         *
         */
        @JvmStatic
        fun buildRestServiceProject(project:Project):List<RestServiceProject> {
            println("buildRestServiceProject====")
            val list:MutableList<RestServiceProject> = ArrayList()
            val modules = ModuleManager.getInstance(project).modules
            println("::::project:::$project")
            println("projectModules:::${project.modules.size}")
            println("moduleManager::::: ${ModuleManager.getInstance(project).modules.size}")
            modules.forEach {
                val restServices = buildRestApiServiceModule(it)
                println("${it.name} ==== restServices::${restServices.size}")
                if(restServices.isNotEmpty()) {
                    list.add(RestServiceProject(it,restServices))
                    // 保存配置
                    // 如果配置不存在，则创建默认配置
                    RestApiModuleSettingState.matchModuleConfig(project,it.name,null,null)
                }

            }
            return  list
        }
        /**
         * resolve all supported service items in module
         *
         */
        fun buildRestApiServiceModule(module:Module): List<RestServiceItem> = SpringResolver(module).findAllSupportedServiceItemsInModule()
        /**
         * resolve all supported service items in project
         *
         */
        fun buildRestApiServiceProject(project:Project): List<RestServiceItem> = SpringResolver(project).findAllSupportedServiceItemsInProject()


        /** 解析controller类 的 Mapping path */
        fun getRequestPaths(psiClass:PsiClass):List<RequestPath> {
            // 解析 class 上的 RequestMapping
            val annotations = psiClass.modifierList?.annotations
            if (annotations.isNullOrEmpty()) {
                return emptyList()
            }
            val requestMappingAnnotation = annotations.find {
                ann ->
                    SpringRequestMethodAnnotation.values().any {
                        it.qualifiedName == ann.qualifiedName || it.qualifiedName.endsWith(ann.qualifiedName!!)
                    }
            } ?: return emptyList()
            return getRequestMappings(requestMappingAnnotation)
        }
        /** 解析Method 的 Mapping path */
        fun getRequestPaths(psiMethod:PsiMethod):List<RequestPath> {
            val annotations = psiMethod.modifierList.annotations

            if (annotations.isEmpty()) {
                return emptyList()
            }
            val requestMappingPaths = ArrayList<RequestPath>()

            annotations.forEach {
                annotation ->
                SpringRequestMethodAnnotation.values().forEach {
                    value ->
                    if(value.qualifiedName == annotation.qualifiedName || value.qualifiedName.endsWith(annotation.qualifiedName!!)) {
                        requestMappingPaths.addAll(getRequestMappings(annotation))
                    }
                }
            }
            return requestMappingPaths
        }

        /**
         * 解析 RequestMapping(GetMapping,PostMapping,PatchMapping,DeleteMapping,PutMapping) 注解的 method, url
         * 当 注解的 value 为数组时，会解析为多个 RequestPath
         * 因此返回值为 List<RequestPath>
         *
         * @param annotation RequestMapping 注解
         * @param defaultValue 默认值 当value 为空时，使用默认值 ‘’
         * @return List<RequestPath>
         */
        private fun getRequestMappings(annotation: PsiAnnotation,defaultValue:String = ""):List<RequestPath> {
//            println("getRequestMappings::${annotation.text}")
            val requestMappings:MutableList<RequestPath> = ArrayList()
            var requestMappingValues = AnnotationHelper.getAnnotationAttributeValues(annotation,"value")
//            println("requestMappingValues::${requestMappingValues}")
            val requestMathodAnnotation = SpringRequestMethodAnnotation.getRequestMethodAnnotation(annotation.qualifiedName!!)
            val requestMappingMethods = ArrayList<String>()
            if(requestMathodAnnotation?.methodName != null) {
                requestMappingMethods.add(requestMathodAnnotation.methodName!!)
            }else {
                requestMappingMethods.addAll( AnnotationHelper.getAnnotationAttributeValues(annotation,"method"))
            }
//            println("requestMappingMethods::${requestMappingMethods}")
            // value 为空时，使用默认值 ‘’
            if(requestMappingValues.isEmpty()) {
                requestMappingValues = listOf(defaultValue)
            }
            // method 为空时， 相当于 RequestMapping 支持所有请求方式
            if(requestMappingMethods.isEmpty()) {
                requestMappingValues.forEach {
                    val requestPath = RequestPath(it,null)
                    requestMappings.add(requestPath)
                }
            } else {
                requestMappingValues.forEach {
                    path ->
                    requestMappingMethods.forEach {
                        method -> requestMappings.add( RequestPath(path,method))
                    }
                }
            }
            return requestMappings
        }

    }
}