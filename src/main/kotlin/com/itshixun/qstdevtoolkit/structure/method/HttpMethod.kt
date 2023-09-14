package com.itshixun.qstdevtoolkit.structure.method

import kotlin.collections.HashMap

enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    PATCH,
    CONNECT;

    companion object {
        val methodMap:Map<String,HttpMethod> = HashMap(8)
        init{
            for(method in values()){
                (methodMap as HashMap<String,HttpMethod>)[method.name] = method
            }
        }
        @JvmStatic
        fun getByRequestMethod(requestMethod:String):HttpMethod?{
            if(requestMethod.isEmpty()){
                return null
            }
            val  split =requestMethod.split(".")
            if(split.size>1){
                return methodMap[split.last().uppercase()]
            }
            return methodMap[requestMethod.uppercase()]
        }
    }
}