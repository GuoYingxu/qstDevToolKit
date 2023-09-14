package com.itshixun.qstdevtoolkit.utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager

class ToolKitUtil {
    companion object {
        /**
         * 项目初始化完成后执行
         */
        @JvmStatic
        fun runWhenInitialized(project: Project, runnable: Runnable) {
            if (project.isDisposed) {
                return
            }

            if(ignorMode()) {
                println("ignored")
                runnable.run()
                return
            }

            if (!project.isInitialized) {
                // 未初始化完成，等待初始化完成后执行
                StartupManager.getInstance(project).runAfterOpened {
                    println("wait for projectInitialized.....")
                    DumbService.getInstance(
                        project
                    ).runWhenSmart(runnable)
                }
                return
            }
        }

        /**
         * 忽略的项目
         */
        fun ignorMode():Boolean = with(ApplicationManager.getApplication()) {
            isUnitTestMode || isHeadlessEnvironment
        }
        @JvmStatic
        fun runWhenProjectIsReady(project: Project?, runnable: Runnable?) {
            DumbService.getInstance(project!!).smartInvokeLater(runnable!!)
        }
        /**
         * 移除冗余的标记 http(s)://domain:port
         */
        @JvmStatic
        fun removeRedundancyMarkup(_pattern: String): String {
            var pattern = _pattern
            val localhostRegex = "(http(s?)://)?(localhost)(:\\d+)?"
            val hostAndPortRegex = "(http(s?)://)?" +
                    "( " +
                    "([a-zA-Z0-9]([a-zA-Z0-9\\\\-]{0,61}[a-zA-Z0-9])?\\\\.)+[a-zA-Z]{2,6} |" +  // domain
                    "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)" +  // ip address
                    ")"
            val localhost = "localhost"
            if (pattern.contains(localhost)) {
                pattern = pattern.replaceFirst(localhostRegex.toRegex(), "")
            }
            // quick test if reg exp should be used
            if (pattern.contains("http:") || pattern.contains("https:")) {
                pattern = pattern.replaceFirst(hostAndPortRegex.toRegex(), "")
            }

            if (!pattern.contains("?")) {
                return pattern
            }
            pattern = pattern.substring(0, pattern.indexOf("?"))
            return pattern
        }

        @JvmStatic
        fun joinPath(prefix:String,url:String):String {
            return if(prefix.endsWith("/") && url.startsWith("/")) {
                prefix + url.substring(1)
            } else if(prefix.endsWith("/") || url.startsWith("/")) {
                prefix + url
            } else {
                "$prefix/$url"
            }
        }

    }


}