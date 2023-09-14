package com.itshixun.qstdevtoolkit.common.resolver

import com.itshixun.qstdevtoolkit.structure.RestServiceItem

interface ServiceResolver {
    /** find all supported service items in module */
    fun findAllSupportedServiceItemsInModule():List<RestServiceItem>
    /** find all supported service items in project */
    fun findAllSupportedServiceItemsInProject():List<RestServiceItem>
}