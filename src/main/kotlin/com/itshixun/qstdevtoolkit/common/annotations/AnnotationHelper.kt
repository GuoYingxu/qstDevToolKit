package com.itshixun.qstdevtoolkit.common.annotations

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiArrayInitializerMemberValue
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiReferenceExpression

/**
 * annotation 处理工具类
 */
class AnnotationHelper {
    companion object {
        /**
         * 获取注解的属性值（支持数组参数）
         *
         * @param annotation 注解
         * @param attrName 属性名
         * @return 属性值
         *
         * 例如 @RequestMapping(value = "/test") 获取到的是 "/test"
         * 例如 @RequestMapping(value = {"/test","/test2"}) 获取到的是 ["/test","/test2"]
         */
        @JvmStatic
        fun getAnnotationAttributeValues(annotation: PsiAnnotation,attrName: String):List<String> {
            val value = annotation.findAttributeValue(attrName) ?: return emptyList()
           // value 值是引用表达式
            if(value is PsiReferenceExpression) {
//                println(" is PsiReferenceExpression")
                return listOf(value.text.removeSurrounding("\""))
            }
            // value 值是字面量表达式
            if(value is PsiLiteralExpression){
//                println(" is PsiLiteralExpression")
                return listOf(value.value.toString().removeSurrounding("\""))
            }
            // value 值是数组表达式
            if(value is PsiArrayInitializerMemberValue) {
//                println(" is PsiArrayInitializerMemberValue")
                return value.initializers.map { it.text.removeSurrounding("\"") }
            }
            return emptyList()

        }

//        /**
//         * 取单个属性值
//         * @param annotation 注解
//         * @param attrName 属性名
//         * @return 属性值
//         */
//        @JvmStatic
//        fun getAnnotatopmAttributeValue(annotation: PsiAnnotation,attrName:String):String {
//            return getAnnotationAttributeValues(annotation,attrName).firstOrNull() ?: ""
//        }
    }
}