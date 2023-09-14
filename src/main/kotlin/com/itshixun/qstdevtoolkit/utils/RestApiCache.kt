package com.itshixun.qstdevtoolkit.utils

import com.itshixun.qstdevtoolkit.structure.RestServiceItem

class RestApiCache {
    companion object {
        private  val apiCache = mutableMapOf<String, RestServiceItem>()

        /**
         * 清空所有
         */
        fun clear(){
            apiCache.clear()
        }

        fun set(key:String,value:RestServiceItem){
            apiCache[key] = value
        }
        fun get(key:String):RestServiceItem?{
            return apiCache[key]
        }
        fun has(key:String):Boolean{
            return apiCache.containsKey(key)
        }

    }
}

