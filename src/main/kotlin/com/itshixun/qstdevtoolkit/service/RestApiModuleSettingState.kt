package com.itshixun.qstdevtoolkit.service

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Tag

@Service(Service.Level.PROJECT)
@State(name = "restApiModuleSettingState", storages = [Storage("restApiModuleSettingState.xml")])
class RestApiModuleSettingState:PersistentStateComponent<RestApiModuleSettingState> {
    @Tag("prefixConfigs")
    var prefixConfigs: Map<String,String> = mutableMapOf()
    @Tag("serverConfigs")
    var serverConfigs: Map<String,String> = mutableMapOf()

//    data class moduleConfig(var host:String,var port:String,var appName:String,var prefix:String)
    companion object {
        @JvmStatic
        fun getInstance(project: Project): RestApiModuleSettingState {
            return project.service<RestApiModuleSettingState>()
        }
        @JvmStatic
        fun matchModuleConfig(project:Project,moduleName:String,value:String?,serverName:String?){
            val shortName = moduleName.replace(Regex("\\.main\$"),"").split(".").last().split('-').last()
            getInstance(project).prefixConfigs= getInstance(project).prefixConfigs.plus( moduleName to (value?:"/api/$shortName"))
            getInstance(project).serverConfigs= getInstance(project).serverConfigs.plus(moduleName to (serverName?:shortName))
        }
        @JvmStatic
        fun getPrefix(moduleName:String,project:Project):String? {
            return getInstance(project).prefixConfigs[moduleName]
        }
        @JvmStatic
        fun getServerName(moduleName: String,project:Project):String? {
            return getInstance(project).serverConfigs[moduleName]
        }
        @JvmStatic
        fun updatePrefix(moduleName:String,project: Project,prefix:String) {
            println("-------update prefix:$prefix")
            getInstance(project).prefixConfigs= getInstance(project).prefixConfigs.plus( moduleName to prefix)
        }
        @JvmStatic
       fun updateServerName(moduleName: String,project: Project,serverName: String) {
            println("-------update serverName:$serverName")
           getInstance(project).serverConfigs = getInstance(project).serverConfigs.plus(moduleName to serverName)
       }

}

    override fun getState(): RestApiModuleSettingState {
        return this
    }

    override fun loadState(state: RestApiModuleSettingState) {
        XmlSerializerUtil.copyBean(state, this)
    }


}