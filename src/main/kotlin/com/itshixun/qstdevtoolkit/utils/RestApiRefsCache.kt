package com.itshixun.qstdevtoolkit.utils

class RestApiRefsCache {

    companion object {
        private var refs:MutableMap<String,List<RestApiRef>> = mutableMapOf()
        fun getApiRefs(key:String):List<RestApiRef>?{
            return refs[key]
        }
        fun setApiRefs(key:String,restApiRefs: List<RestApiRef>) {
            refs[key] = restApiRefs
        }
        fun hasRefsCache(key:String):Boolean{
            return refs.containsKey(key)
        }
    }

}