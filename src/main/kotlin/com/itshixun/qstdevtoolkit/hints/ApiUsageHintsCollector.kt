package com.itshixun.qstdevtoolkit.hints

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.InlayPresentationFactory
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.openapi.editor.BlockInlayPriority
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierListOwner
import com.intellij.ui.awt.RelativePoint
import com.itshixun.qstdevtoolkit.utils.*
import com.itshixun.qstdevtoolkit.window.RestApiRefsPopupFactory
import java.awt.BorderLayout
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JPanel

class ApiUsageHintsCollector(editor: Editor, private val document: Document, private val moduleName: String):FactoryInlayHintsCollector(editor){
    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        if(editor.project == null) {
            return false
        }
        var hints = retriveHints(element, editor,moduleName)
        if(hints != null) {
            sink.addBlockElement(hints.first, true,true, BlockInlayPriority.ANNOTATIONS, hints.second)
        }
        return true

    }
    private fun InlayPresentation.withIcon(icon: Icon): InlayPresentation =
        factory.seq(factory.smallScaledIcon(icon), this)
    /**
     * 从element中获取hints
     * TODO 后期修改未从缓存取
     */
    private fun retriveHints(element: PsiElement, editor: Editor, moduleName: String):Pair<Int,InlayPresentation>? {
        var paire:Pair<Int,InlayPresentation>? = null
        if (element is PsiModifierListOwner && element is PsiMethod) {
//            println("------- getMethod,${element.name}-----------------")
            element.containingClass?.let {
                psiClass ->
                run {
//                    println("psiClass.name:${psiClass.name}")
                    val path = "$moduleName#${psiClass.name}#${element.name}"
                    RestApiCache.get(path)?.let {
                        it.checkRefs()
                        if(it.url.isNullOrBlank()) {
                            return null
                        }
                        var text = factory.smallTextWithoutBackground(it.url!!)
                        text= text.withIcon(it.marker.icon)
                        val presentation = factory.referenceOnHover( text
                                ,
//                            ) {
//                                event,_ ->
//                                Runnable{
//                                    val action = ActionManager.getInstance().getAction("ShowApiUsage")
//                                    editor.component.putUserData(RestApiServiceDataKey.USAGE_ITEM,it)
//                                    invokeAction(action,editor.component,EDITOR_INLAY,event,null)
                                object:InlayPresentationFactory.ClickListener{
                                    override fun onClick(event: MouseEvent, translated: Point) {
                                        val popupFactory= JBPopupFactory.getInstance()
                                        val panel = JPanel(BorderLayout())
                                        var refsPanel =  RestApiRefsPopupFactory.createPanel(it)
                                        panel.setSize(400,300)
                                        panel.add(refsPanel, BorderLayout.CENTER)
                                        popupFactory.createComponentPopupBuilder(panel,refsPanel)
                                            .setTitle("引用详情")
                                            .setMovable(true)
                                            .setResizable(true)
                                            .createPopup()
                                            .show(RelativePoint(event))
                                    }
                                })
                        val modifierList = element.modifierList
                        val textRange = modifierList.textRange
                        if(textRange!=null) {
                            val offset = textRange.startOffset
                            val width = EditorUtil.getPlainSpaceWidth(editor)
                            val line = document.getLineNumber(offset)
                            val startOffset = document.getLineStartOffset(line)
                            val column = offset - startOffset
                            val shifted = factory.inset(presentation, left = width * column)
                            paire = Pair(offset, shifted)
                        }
                    }
                }
            }
        }
        return paire
    }

}
