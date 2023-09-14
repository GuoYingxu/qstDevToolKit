package com.itshixun.qstdevtoolkit.utils

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.itshixun.qstdevtoolkit.structure.method.HttpMethod
import javax.swing.Icon

class ToolKitIconsUtil {
    companion object {
        val MOUDLE_ICON = AllIcons.Modules.UnloadedModule
//        val REFRESH_ICON = AllIcons.Actions.Refresh
//        val SERVICE_ICON = IconLoader.getIcon("/icons/service.png", ToolKitIconsUtil::class.java)
        val GET = IconLoader.getIcon("/icons/g.png", ToolKitIconsUtil::class.java)
        val POST = IconLoader.getIcon("/icons/p.png", ToolKitIconsUtil::class.java)
        val PUT = IconLoader.getIcon("/icons/p2.png", ToolKitIconsUtil::class.java)
        val DELETE = IconLoader.getIcon("/icons/d.png", ToolKitIconsUtil::class.java)
        val PATCH = IconLoader.getIcon("/icons/p3.png", ToolKitIconsUtil::class.java)
        val REQUEST = IconLoader.getIcon("/icons/undefined.png", ToolKitIconsUtil::class.java)

        fun getIcon(method:HttpMethod?):Icon {
            if(method == null) return REQUEST
            return when(method) {
                HttpMethod.GET -> GET
                HttpMethod.POST -> POST
                HttpMethod.PUT -> PUT
                HttpMethod.DELETE -> DELETE
                HttpMethod.PATCH -> PATCH
                else -> REQUEST
            }
        }
    }
}