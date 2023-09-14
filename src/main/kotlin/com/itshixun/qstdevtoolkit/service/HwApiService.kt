package com.itshixun.qstdevtoolkit.service

import com.huaweicloud.sdk.core.auth.BasicCredentials
import com.huaweicloud.sdk.core.auth.ICredential
import com.huaweicloud.sdk.core.exception.ConnectionException
import com.huaweicloud.sdk.core.exception.RequestTimeoutException
import com.huaweicloud.sdk.core.exception.ServiceResponseException
import com.huaweicloud.sdk.projectman.v4.*
import com.huaweicloud.sdk.projectman.v4.model.*
import com.huaweicloud.sdk.projectman.v4.region.ProjectManRegion
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.itshixun.qstdevtoolkit.config.QstToolkitConfig
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel


@Service(Service.Level.PROJECT)
class HwApiService {
    companion object {
        const val TOOLWINDOW_ID = "HwTasks"
    }
    var projectList:ArrayList<ListProjectsV4ResponseBodyProjects> = ArrayList()
    var taskList:ArrayList<ListIssueItemResponse> = ArrayList()


    var projectsComboxModel = DefaultComboBoxModel(projectList.toArray())
    var taskListModel = DefaultListModel<ListIssueItemResponse>()
    fun clear() {
        projectList.clear()
        taskList.clear()
    }
    fun getProjectDataList(){
        projectList = ArrayList()
    }
    fun getTaskDataList(){
        taskList = ArrayList()
    }
    fun updateTasks(project:Project,hwProject:ListProjectsV4ResponseBodyProjects) {
        val ak = project.service<QstToolkitConfigService>().get(QstToolkitConfig.HW_ACCESS_KEY, "")
        val sk = project.service<QstToolkitConfigService>().get(QstToolkitConfig.HW_SECRET_KEY, "")
        if (ak == "" || sk == "") {
            println("not fond ak or sk")
            //  Noti.notify("请先配置AK和SK")
            val content ="请先配置华为云ak/sk"
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(content, NotificationType.WARNING)
                .notify(project);

            return
        }
        val auth: ICredential = BasicCredentials()
                .withAk(ak)
                .withSk(sk)

        val client = ProjectManClient.newBuilder()
                .withCredential(auth)
                .withRegion(ProjectManRegion.valueOf("cn-north-1"))
                .build()
        val request = ListIssuesV4Request()
        val body = ListIssueRequestV4()
        body.limit = 100
        body.offset  = 0
        body.statusIds = mutableListOf(1,2)
        body.trackerIds = mutableListOf(2,3)
        request.withProjectId(hwProject.projectId)
        val  assignedUsers = project.service<QstToolkitConfigService>().get(QstToolkitConfig.HW_PROJECT_ASSIGNED_USER,"")
        if(assignedUsers.isNotEmpty()) {
            val assignedIds = assignedUsers.split(',').map {
                // it 转Int 类型
                 it.toInt()
            }
            body.assignedIds = assignedIds
        }
        request.withBody(body)
        try {
            val response = client.listIssuesV4(request)
//            println(response.toString())
            taskListModel.clear()
            taskListModel.addAll(response.issues)
            println("update issue list ${response.issues.size}")

        } catch (e: ConnectionException) {
            e.printStackTrace()
        } catch (e: RequestTimeoutException) {
            e.printStackTrace()
        } catch (e: ServiceResponseException) {
            e.printStackTrace()
            println(e.httpStatusCode)
            println(e.requestId)
            println(e.errorCode)
            println(e.errorMsg)
        }
    }

    /**
     * 查询项目列表
     */
    fun updateProjects(project: Project, refresh:Boolean = false) {
        // update projects

        var ak = project.service<QstToolkitConfigService>().get(QstToolkitConfig.HW_ACCESS_KEY, "")
        var sk = project.service<QstToolkitConfigService>().get(QstToolkitConfig.HW_SECRET_KEY, "")
        if (ak == "" || sk == "") {
            println("not fond ak or sk")
          //  Noti.notify("请先配置AK和SK")
            val content ="请先配置华为云ak/sk"
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(content, NotificationType.WARNING)
                .notify(project)
            return
        }
        println("ak:$ak")
        println("sk:$sk")
        val auth  = BasicCredentials()
            .withAk(ak)
            .withSk(sk)

         val client = ProjectManClient.newBuilder()
            .withCredential(auth)
            .withRegion(ProjectManRegion.valueOf("cn-north-1"))
            .build()

        val request =ListProjectsV4Request()
        request.withOffset(0)
        request.withLimit(100)
        try {
            val response = client.listProjectsV4(request)
//            println(response.toString());
            projectList.clear()

            response.projects.forEach {
                val configedProjectIds = project.service<QstToolkitConfigService>().get(QstToolkitConfig.HW_PROJECT_IDS, "")
//               println("-----configProjectIds $configedProjectIds")
                if(configedProjectIds == ""){
                    projectList.addAll(response.projects)
//                    projectsComboxModel.addElement(it)
                }else {
                    // 根据配置的id 过滤
                    val ids = configedProjectIds?.split(",")
                    ids?.forEach { id ->
                        if (id == it.projectId) {
                            projectList.add(it)
//                            projectsComboxModel.addElement(it)
                        }
                    }
                }

            }
            projectsComboxModel.addAll(projectList)
//            = DefaultComboBoxModel(projectList.toArray())
        } catch (e:ConnectionException) {
            e.printStackTrace()
        } catch (e:RequestTimeoutException ) {
            e.printStackTrace()
        } catch (e:ServiceResponseException ) {
            e.printStackTrace()
            println(e.getHttpStatusCode())
            println(e.getRequestId())
            println(e.getErrorCode())
            println(e.getErrorMsg())
        }
    }




}