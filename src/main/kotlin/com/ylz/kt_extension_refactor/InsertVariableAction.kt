package com.ylz.kt_extension_refactor

import com.intellij.find.FindManager
import com.intellij.find.impl.FindManagerImpl
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.usages.Usage
import com.intellij.usages.impl.UsageViewImpl
import org.jetbrains.kotlin.psi.KtFile
import org.junit.Assert

class InsertVariableAction : AnAction() {

    override fun actionPerformed(action: AnActionEvent) {

        findAllUsages(action.getData(LangDataKeys.PSI_ELEMENT)!!, action.project!!)
//        getImportStringFromKtFile(action).firstOrNull()?.let {
//        }

//        AndroidGotoDeclarationHandler().getGotoDeclarationTargets(
//            action.getData(LangDataKeys.PSI_ELEMENT) as? KtSimpleNameExpression,
//            0,
//            action.getData(LangDataKeys.EDITOR)
//        )?.let {
//            // 这里可以获取到目标元素，可以进行一些操作
//            println(it)
//        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
    }

    private fun findAllUsages(targetElement: PsiElement, project: Project): MutableSet<Usage> {
        val usagesManager = (FindManager.getInstance(project) as FindManagerImpl).findUsagesManager
        val handler = usagesManager.getFindUsagesHandler(targetElement, false)
        Assert.assertNotNull("Cannot find handler for: $targetElement", handler)
        val usageView =
            usagesManager.doFindUsages(
                handler!!.primaryElements,
                handler.secondaryElements,
                handler,
                handler.findUsagesOptions,
                false
            ) as UsageViewImpl

        return usageView.usages
    }

    private fun getImportStringFromKtFile(e: AnActionEvent): List<PsiElement> {
        val psiFile = e.getData(LangDataKeys.PSI_FILE)

        return (psiFile as? KtFile)?.let {
            it.importList?.imports?.map { item ->
                item.alias as PsiElement
            } ?: emptyList()
        } ?: emptyList()
    }
}