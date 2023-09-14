package com.itshixun.qstdevtoolkit.structure

import com.alibaba.fastjson.JSON
import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocCommentOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.itshixun.qstdevtoolkit.config.QstToolkitConfig
import com.itshixun.qstdevtoolkit.service.QstToolkitConfigService
import com.itshixun.qstdevtoolkit.service.RestApiModuleSettingState
import com.itshixun.qstdevtoolkit.structure.method.HttpMethod
import com.itshixun.qstdevtoolkit.utils.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.swing.Icon

/**
 * RestServiceItem 具有跳转功能的树节点
 * @extends NavigationItem
 * @param psiElement  element 对象
 * @param requestMethod 请求方法
 * @param urlPath 请求路径
 */
class RestServiceItem(var psiElement:PsiElement,private var requestMethod:String?,private var urlPath:String,var module:Module):NavigationItem {
    var psiMethod: PsiMethod? = null
    var method: HttpMethod? = null
    var url: String? = null
    private var navigationItem: Navigatable? = null
    var description: String = ""
    private var prefix: String = ""
    private var psiClass: PsiClass? = null
    private var apiModuleName:String
//    val LOG = logger<RestServiceItem>()
    /**
     * 对应服务器端 的json对象
     */
    data class ApiJson(var uri:String?,
        var puri:String?,
        var method:String?,
        var server:String?,
        var version:String?,
        var file:String?
        )

    /**
     * lineMarker 数据
     */
    data class MarkerMessage(
        var tooltip:String="ApiService Unavailable",
        var enabled:Boolean = false,
        var refs:List<RestApiRef>?=null,
        var icon: Icon = AllIcons.General.Note
    )
    /**
     * method path
     * 用于标识方法的唯一路径
     */
    var path: String? = null

    var marker = MarkerMessage()

    init {

//        LOG.info("module.name = ${module.name}")
        val shortName = module.name.replace(".main", "").split('.').last().split('-').last()
        apiModuleName = RestApiModuleSettingState.getServerName(module.name,psiElement.project) ?: shortName
         prefix = RestApiModuleSettingState.getPrefix(module.name, psiElement.project ) ?: "/api/${apiModuleName}"
        if (psiElement is PsiMethod) {
            this.psiMethod = psiElement as PsiMethod
            this.psiClass = psiMethod!!.containingClass
            path = "${module.name}#${psiClass!!.name}#${psiMethod!!.name}"
        }
        if (requestMethod != null) {
            this.method = HttpMethod.getByRequestMethod(requestMethod!!)
        }
        this.url = "$prefix/${urlPath.replace(Regex("^\\/"),"")}"
//        LOG.warn(this.url)
        if (psiElement is Navigatable) {
            this.navigationItem = psiElement as Navigatable
        }

        /**
         * 获取接口注释
         */
        (psiElement as? PsiDocCommentOwner)?.docComment?.descriptionElements?.map {
            if (it.text.isNotBlank() && !it.text.startsWith("@")) {
//                if(description == null) {
                description = it.text.trim()
                return@map
//                }else {
//                    description = "$description${it.text}"
//                }
            }
        }

    }
    fun updateModuleConfig(configModuleName:String,configPrefix:String) {
        println("---- updateModuleConfig :$configPrefix")
        if(this.apiModuleName != configModuleName) {
            this.apiModuleName  = configModuleName
        }
        if(this.prefix != configPrefix) {
            println("---- prefix :$configPrefix")
            this.prefix = configPrefix
        }
        if(this.url != "$prefix/${urlPath.replace(Regex("^\\/"),"")}") {

            this.url =  "$prefix/${urlPath.replace(Regex("^\\/"),"")}"
        }
    }

    fun getApiJson():ApiJson {
        val filePath = "src/main" + this.psiClass?.containingFile?.virtualFile?.path?.split("src/main")?.last()
        return ApiJson(this.url,this.getPurlUrl()!!,this.method?.name,this.apiModuleName,"",filePath )
    }
    private fun setRefsMarker(refs:List<RestApiRef>) {
        if(refs.isEmpty()) {
            marker.tooltip="没有被使用"
            marker.enabled =true
            marker.icon = AllIcons.General.Warning
        }else {
            var count = 0
            refs.forEach {
                count += it.files.split(';').size
            }
            marker.tooltip = "被使用${count}次"
            marker.refs = refs
            marker.enabled = true
            marker.icon = AllIcons.General.Information
        }
    }

    fun checkRefs() {
        if(path == null) return
        // 先从缓存取
        if(RestApiRefsCache.hasRefsCache(path!!)) {
            val refs = RestApiRefsCache.getApiRefs(path!!)?: emptyList()
            setRefsMarker(refs)
            return
        }

        val API_CHECK_REF = "apimanage/api_call_infomations"
        // 检查配置 apiServerHost
        val config =
            ApplicationManager.getApplication().getService<QstToolkitConfigService>(QstToolkitConfigService::class.java)
        val host = config.get(QstToolkitConfig.REST_API_HOST, "")
        if (host == ""){
            println("not config apiServerHost")
            return
        }


        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val param= mutableMapOf<String,String>()
        param["method"] = this.method?.name?:""
        param["url"] = this.url?:""
        param["moduleName"] = apiModuleName
        val  body = JSON.toJSONString(param).toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(ToolKitUtil.joinPath(host, API_CHECK_REF))
            .post(body)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val result = response.body?.string()
            println("result:::$result")
            val res:RestUsageResponse = JSON.parseObject(result, RestUsageResponse::class.java)
            if (res.code == 200) {
                val refs: List<RestApiRef> = res.data
                setRefsMarker(refs)
                RestApiRefsCache.setApiRefs(path!!,refs)
            }
        }catch (e:Exception) {
            println("request apiRefs error:$path")

            e.printStackTrace()
        }
    }
    private fun getFullUrl():String? {
        return url
        // TODO 增加url 配置 返回全路径
    }
//    private fun getKey():String {
//        return this.getFullUrl()+this.method?.name
//    }

    private fun getPurlUrl():String? {
        return url?.replace("\\{([\\s\\S]*?)}".toRegex(), "{params}")
    }

    override fun navigate(requestFocus: Boolean) {
        if(navigationItem != null) {
            navigationItem!!.navigate(requestFocus)
        }
    }


    override fun canNavigate(): Boolean {
        return navigationItem!!.canNavigate()
    }

    override fun canNavigateToSource(): Boolean = true

    override fun getName(): String {
        return if(description.isNotBlank()) {
            "$url  ($description)"
        }else {
            url?:""
        }
    }

    override fun getPresentation():ItemPresentation = object:ItemPresentation {
        override fun getLocationString(): String {
            if(psiElement is PsiMethod) {
//                val  psiMethod = psiElement as PsiMethod
//                if(module != null) {
//                    return "(#$description)"
//                }
                return "(#$description)"
            }
            return "(#null)"
        }

        override fun getIcon(unused: Boolean): Icon {
            return ToolKitIconsUtil.getIcon(method)
        }

        override fun getPresentableText(): String? {
            return url
        }
    }


}
