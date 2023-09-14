package com.itshixun.qstdevtoolkit.utils

import com.itshixun.qstdevtoolkit.structure.RestServiceProject

class RestProjectCache {
    companion object {
        private val data:HashMap<String,RestServiceProject> = HashMap()

        fun get(key:String):RestServiceProject? {
            return data[key]
        }
        fun set(key:String,project:RestServiceProject) {
            data[key] = project
        }
    }
}