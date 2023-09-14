package com.itshixun.qstdevtoolkit.action

import com.alibaba.fastjson.JSON
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.itshixun.qstdevtoolkit.config.QstToolkitConfig
import com.itshixun.qstdevtoolkit.service.QstToolkitConfigService
import com.itshixun.qstdevtoolkit.structure.RestServiceItem
import com.itshixun.qstdevtoolkit.utils.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class UploadModuleApiAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val moduleNodes = RestApiServiceDataKey.SERVICE_MODULE.getData(e.dataContext)
        if (moduleNodes != null) {
            for (node in moduleNodes) {
                Runnable {
                    uploadApiByModule(node.project.appName, node.project.serviceItems, e.project!!)
                }.run()
            }
        }
    }

    private fun uploadApiByModule(moduleName: String, items: List<RestServiceItem>, project: Project) {
        val API_POST = "/apimanage/upload_module_apis"
        val config = project.service<QstToolkitConfigService>()
//            ApplicationManager.getApplication().getService<QstToolkitConfigService>(QstToolkitConfigService::class.java)
        val host = config[QstToolkitConfig.REST_API_HOST, ""]
        if (host == "") {
            println("not config apiServerHost")
            val content = "请先配置API 服务地址"
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(content, NotificationType.WARNING)
                .notify(project)
            return
        }
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val param = mutableMapOf<String, Any>()
        param["list"] = items.map { it.getApiJson() }
        param["moduleName"] = moduleName
        val body = JSON.toJSONString(param).toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(ToolKitUtil.joinPath(host, API_POST))
            .post(body)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val result = response.body?.string()
            val res: UploadResponse = JSON.parseObject(result, UploadResponse::class.java)
            if (res.code == 200) {
                println("upload complet")
                val content = "上传成功！"
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Custom Notification Group")
                    .createNotification(content, NotificationType.INFORMATION)
                    .notify(project)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val content = "上传失败！"
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(content, NotificationType.ERROR)
                .notify(project)
            println("upload Api error")
        }

    }
}
    data class  UploadResponse(var code:Int,var message:String?)