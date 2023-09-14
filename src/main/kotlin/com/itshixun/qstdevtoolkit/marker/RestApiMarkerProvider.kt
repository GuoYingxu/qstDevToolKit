package com.itshixun.qstdevtoolkit.marker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.*
import com.itshixun.qstdevtoolkit.common.annotations.SpringRequestMethodAnnotation
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import com.itshixun.qstdevtoolkit.utils.RestApiCache


class RestApiMarkerProvider: LineMarkerProvider{
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiIdentifier>? {
//        var psiIdentifier:PsiIdentifier? = null
//        if(element is PsiAnnotation ) {
//            val annotations =  element.parent
//            if(annotations is PsiModifierList) {
//                val owner = annotations.parent
//                if (owner is PsiMethod) {
//                    element.nameReferenceElement?.let {
//                        it.children.forEach { child ->
//                            if (child is PsiIdentifier) {
//                                SpringRequestMethodAnnotation.values().forEach { value ->
//                                    if (value.qualifiedName == child.text || value.qualifiedName.endsWith(
//                                            child.text
//                                        )
//                                    ) {
//                                        psiIdentifier = child
//                                        return@forEach
//                                    }
//                                }
//                                return@forEach
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            if(psiIdentifier!=null) {
//                println("psiIdentifier:::${psiIdentifier!!.text}")
//                return LineMarkerInfo(psiIdentifier!!,psiIdentifier!!.textRange)
//            }
//
//        }
        return null
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        elements.forEach { element ->
            searchServiceItem(element)?.let { item ->
                run {
                    getIdentifier(element)?.let {
                        NavigationGutterIconBuilder.create(item.marker.icon)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTarget(element)
                            .setTooltipText(item.marker.tooltip)
                            .createLineMarkerInfo(it).let { info ->
                                result.add(info)
                            }
                    }
                }
            }

        }
    }
    private fun getIdentifier(element: PsiElement):PsiIdentifier? {
        var psiIdentifier:PsiIdentifier? = null
        if(element is PsiAnnotation ) {
            val annotations =  element.parent
            if(annotations is PsiModifierList) {
                val owner = annotations.parent
                if (owner is PsiMethod) {
                    element.nameReferenceElement?.let {
                        it.children.forEach { child ->
                            if (child is PsiIdentifier) {
                                SpringRequestMethodAnnotation.values().forEach { value ->
                                    if (value.qualifiedName == child.text || value.qualifiedName.endsWith(
                                            child.text
                                        )
                                    ) {
                                        psiIdentifier = child
                                        return@forEach
                                    }
                                }
                                return@forEach
                            }
                        }
                    }
                }
            }
        }
        return psiIdentifier
    }
    private fun searchServiceItem(element: PsiElement):RestServiceItem? {
        var restServiceItem:RestServiceItem? = null
        if(element is PsiAnnotation) {
            val annotations =  element.parent
            if(annotations is PsiModifierList) {
                val owner = annotations.parent
                if(owner is PsiMethod) {
                    val psiClass:PsiClass? = owner.containingClass
                    if(psiClass!=null){
                        val module = ProjectFileIndex.getInstance(element.project).getModuleForFile(psiClass.containingFile.virtualFile)
                        if(module !=null){
                            val path = "${module.name}#${psiClass.name}#${owner.name}"
                            restServiceItem = RestApiCache.get(path)
                            restServiceItem?.checkRefs()
                        }
                    }
                }
            }
        }
        return restServiceItem
    }

}