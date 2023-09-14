package com.itshixun.qstdevtoolkit.action
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShowApiUsageAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

//            val popupFactory = JBPopupFactory.getInstance()
//            val panel = JPanel(BorderLayout())
//            val editor = e.getData(CommonDataKeys.EDITOR)
//        val restServiceItem = editor?.component?.getUserData(RestApiServiceDataKey.USAGE_ITEM)
//        RestApiServiceDataKey.USAGE_ITEM?.let {
//            var refsPanel = RestApiRefsPopupFactory.createPanel(editor.component.getUserData(RestApiServiceDataKey.USAGE_ITEM))
//            panel.setSize(400, 300)
//            panel.add(refsPanel, BorderLayout.CENTER)
//            popupFactory.createComponentPopupBuilder(panel, refsPanel)
//                .setTitle("引用详情")
//                .setMovable(true)
//                .setResizable(true)
//                .createPopup()
//                .show(e);
//        }
    }
}