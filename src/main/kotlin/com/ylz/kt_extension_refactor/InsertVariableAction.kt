package com.ylz.kt_extension_refactor

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.psi.KtClass

import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.*
import com.intellij.openapi.ui.messages.MessageDialog
import com.intellij.openapi.ui.popup.BalloonBuilder
import com.intellij.openapi.util.NlsContexts

class InsertVariableAction : AnAction() {

//    private var ktClass: KtClass? = null

    override fun actionPerformed(action: AnActionEvent) {
        getPsiClassFromEvent(action)
    }

    override fun update(e: AnActionEvent) {
//        ktClass = getPsiClassFromEvent(e)

//        e.presentation.isEnabled = ktClass != null &&
//                !ktClass.isEnum() && !ktClass.isInterface()
    }

    private fun getPsiClassFromEvent(e: AnActionEvent)/*: KtClass?*/ {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return

        val project = editor.project ?: return

        val psiFile = e.getData(LangDataKeys.PSI_FILE)
        Messages.showDialog(
                "File: ${psiFile?.name}",
                "Title",
                emptyArray<String>(),
                0,
                null
        )
//            println("KtFile: ${psiFile.virtualFilePath}")

//        Psi
//        val location: Location = Location.fromEditor(editor, project)
//        val psiElement = psiFile.findElementAt(0)!!

//        PsiDocumentManager.getInstance(e.project!!).getDocument(psiFile).
//        val psiMethod: PsiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java)!!
//        return KtClassHelper.getKtClassForElement(psiElement)
    }
}