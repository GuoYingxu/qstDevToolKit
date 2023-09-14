package com.itshixun.qstdevtoolkit.utils



//class PsiUtils {
//
////    companion object {
//////        val LOG = logger<PsiUtils>()
////
////        fun joinPath( baseurl:String,url:String) :String {
////
////            if(baseurl.isEmpty()) return url;
////            if(url.isEmpty()) return baseurl;
////            if(baseurl.endsWith("/") && url.startsWith("/")) {
////                return baseurl+url.substring(1)
////            }else if(!baseurl.endsWith("/") && !url.startsWith("/")) {
////                return "$baseurl/$url"
////            }else {
////                return baseurl+url
////            }
////        }
////    }
//}
data  class  RestUsageResponse(var code:Int,var data:List<RestApiRef>)
data class RestApiRef(var clientName:String, var files:String)
