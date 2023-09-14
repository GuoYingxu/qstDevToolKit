package com.itshixun.qstdevtoolkit.common.annotations

/**
 * spring request method annotation
 * @param qualifiedName annotation qualified name
 * @param methodName {optional} request method name
 *
 * values: [RequestMapping,GetMapping,PostMapping,PatchMapping,DeleteMapping,PutMapping]
 *
 */
enum class SpringRequestMethodAnnotation(var qualifiedName:String,var methodName:String?){
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping",null),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping","GET"),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping","POST"),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping","PUT"),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping","DELETE"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping","PATCH"),
    ;

    companion object {
        fun getRequestMethodAnnotation(qualifiedName:String):SpringRequestMethodAnnotation? {
//            println("qualifiedName::${qualifiedName}")
            values().forEach {
                if (it.qualifiedName == qualifiedName || it.qualifiedName.endsWith(qualifiedName)) {
                    return it
                }
            }
            return null
        }

    }

}