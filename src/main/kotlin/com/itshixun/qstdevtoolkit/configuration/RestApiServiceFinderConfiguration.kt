package com.itshixun.qstdevtoolkit.configuration

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.itshixun.qstdevtoolkit.structure.method.HttpMethod

/**
 * RequestMappingConfiguration
 * Configuration for file type filtering popup in "Go to | Service" action.
 *
 */
@Service(Service.Level.PROJECT)
@State(name = "RequestMappingConfiguration",storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class RestApiServiceFinderConfiguration():ChooseByNameFilterConfiguration<HttpMethod>() {

    companion object {

        fun getInstance(project: Project?): RestApiServiceFinderConfiguration? {
//        return ServiceManager.getService(
//            project!!,
//            RestApiServiceFinderConfiguration::class.java
//        )
            return project!!.getService(RestApiServiceFinderConfiguration::class.java)
        }
    }
    override fun nameForElement(type: HttpMethod): String = type.name

}