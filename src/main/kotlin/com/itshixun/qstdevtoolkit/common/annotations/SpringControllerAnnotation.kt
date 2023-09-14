package com.itshixun.qstdevtoolkit.common.annotations

/**
 * Controller 枚举
 *  values: 【CONTROLLER】 【FEIGN_CLIENT】 【REST_CONTROLLER】
 */
enum class SpringControllerAnnotation(private val shortName:String,private val qualifiedName:String):PathMappingAnnotation {
    CONTROLLER("Controller","org.springframework.stereotype.Controller"),
    FEIGN_CLIENT("FeignClient","org.springframework.cloud.openfeign.FeignClient"),
    REST_CONTROLLER("RestController","org.springframework.web.bind.annotation.RestController"),
    ;

    override fun getQualifiedName(): String {
        return qualifiedName
    }

    override fun getShortName(): String {
        return shortName
    }

}