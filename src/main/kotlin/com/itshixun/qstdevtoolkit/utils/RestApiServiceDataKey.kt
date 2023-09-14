package com.itshixun.qstdevtoolkit.utils

import com.intellij.openapi.actionSystem.DataKey
import com.itshixun.qstdevtoolkit.structure.ProjectNode
import com.itshixun.qstdevtoolkit.structure.RestServiceItem

/**
 * DataKey
 */
class RestApiServiceDataKey {
    companion object {
        /**
         * 保存RestServiceItem的List
         */
        var SERVICE_ITEMS = DataKey.create<List<RestServiceItem>>("API_SERVICE_ITEMS")
        var SERVICE_MODULE = DataKey.create<List<ProjectNode>>("API_SERVICE_MODULE")
//        var USAGE_ITEM= Key.create<RestServiceItem>("API_USAGE_ITEM")
    }
}