package com.itshixun.qstdevtoolkit.structure

import com.intellij.openapi.module.Module
import com.itshixun.qstdevtoolkit.service.RestApiModuleSettingState
import com.itshixun.qstdevtoolkit.utils.RestProjectCache


/**
 * 对module模块的封装
 * 保存项目模块的请求地址等信息
 */

class RestServiceProject(module: Module, var serviceItems: List<RestServiceItem>) {
    var port = "8080"
    var appName = ""
    var packageName = ""
    var moduleName = ""
    var module:Module? = module
    var prefix = ""
    init {
        this.moduleName = module.name
        this.appName = module.name
        RestProjectCache.set(this.moduleName,this)
        update()
    }
    fun update() {
         RestApiModuleSettingState.getServerName(this.moduleName,module!!.project)?.let {
             this.appName = it
         }
         RestApiModuleSettingState.getPrefix(this.moduleName,module!!.project)?.let {
             this.prefix = it
         }
        println("----update:::$moduleName")
        serviceItems.forEach {
            it.updateModuleConfig(this.appName,this.prefix)
        }

    }

    override fun toString(): String {
        return "${this.appName}:${this.port}"
    }
}