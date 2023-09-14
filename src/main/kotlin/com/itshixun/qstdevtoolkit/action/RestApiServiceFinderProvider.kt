package com.itshixun.qstdevtoolkit.action

import com.intellij.ide.util.gotoByName.ChooseByNameViewModel
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.psi.PsiElement
import com.intellij.util.Processor
import com.itshixun.qstdevtoolkit.utils.ToolKitUtil

class RestApiServiceFinderProvider(context:PsiElement):DefaultChooseByNameItemProvider(context) {

//    private fun buildPatternMatcher(pattern: String, caseSensitivity: MatchingCaseSensitivity): MinusculeMatcher {
//        return NameUtil.buildMatcher(pattern, caseSensitivity)
//    }

    override fun  filterNames(
        base: ChooseByNameViewModel,
        names: Array<out String>,
        pattern: String
    ): MutableList<String> {
        return super.filterNames(base, names, pattern)
    }

    override fun filterElements(
        base: ChooseByNameViewModel,
        pattern: String,
        everywhere: Boolean,
        indicator: ProgressIndicator,
        consumer: Processor<Any?>
    ): Boolean {
        val pattern1 = ToolKitUtil.removeRedundancyMarkup(pattern)
        return super.filterElements(base, pattern1, everywhere, indicator, consumer)
    }
}