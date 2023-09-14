package com.itshixun.qstdevtoolkit.structure

/**
 * RequestPath class
 * 一个请求对象 包含 请求方法（可以为空），请求路径
 */
class RequestPath(var path:String, var method:String?){
//    /**
//     * 拼接请求路径
//     * @param classRequestPath controller类的请求路径
//     */
//    fun concat(classRequestPath:RequestPath){
//        var classUri = classRequestPath.path
//        if(!classUri.startsWith("/")){
//            classUri = "/${classUri}"
//        }
//        if(!classUri.endsWith("/")){
//            classUri = "${classUri}/"
//        }
//        if(this.path.startsWith("/")){
//            this.path = this.path.substring(1,this.path.length)
//        }
//        this.path = "${classUri}${this.path}"
//    }

    override fun toString(): String {
        return "$method:$path"
    }
}