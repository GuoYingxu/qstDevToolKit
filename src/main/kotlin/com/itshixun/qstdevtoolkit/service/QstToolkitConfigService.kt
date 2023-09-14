package com.itshixun.qstdevtoolkit.service

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.*

@Service(Service.Level.PROJECT)
class QstToolkitConfigService {

    private val propertiesComponent = PropertiesComponent.getInstance()

    fun save(key: String?, value: String?) {
        propertiesComponent.setValue(key!!, value)
    }

    fun save(key: String?, value: Boolean) {
        propertiesComponent.setValue(key!!, value)
    }

    fun save(key: String?, value: Int) {
        propertiesComponent.setValue(key!!, value.toString())
    }

    operator fun get(key: String?, defaultValue: String?): String {
        return propertiesComponent.getValue(key!!, defaultValue!!)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return propertiesComponent.getBoolean(key!!, defaultValue)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return propertiesComponent.getInt(key!!, defaultValue)
    }

    fun clear(key: String?) {
        propertiesComponent.unsetValue(key!!)
    }
}
