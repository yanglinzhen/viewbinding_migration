package com.ylz.kt_extension_refactor

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.Messages
import org.jetbrains.kotlin.psi.KtFile

class InsertVariableAction : AnAction() {

//    private var ktClass: KtClass? = null

    override fun actionPerformed(action: AnActionEvent) {
        getPsiClassFromEvent(action)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
//        ktClass = getPsiClassFromEvent(e)

//        e.presentation.isEnabled = ktClass != null &&
//                !ktClass.isEnum() && !ktClass.isInterface()
    }

    private fun getPsiClassFromEvent(e: AnActionEvent)/*: KtClass?*/ {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val psiFile = e.getData(LangDataKeys.PSI_FILE)
        val currentProj = e.project

        (psiFile as? KtFile)?.let {
            """
                File name: ${it.containingFile.name}
                Imports: ${it.importList?.imports?.joinToString { import ->
                    import.containingFile.name
                }}
            """.trimIndent()
        }?.let {
            Messages.showMessageDialog(
                    currentProj,
                    it,
                    "Title",
                    Messages.getInformationIcon()
            )
        }
//            println("KtFile: ${psiFile.virtualFilePath}")

//        Psi
//        val location: Location = Location.fromEditor(editor, project)
//        val psiElement = psiFile.findElementAt(0)!!

//        PsiDocumentManager.getInstance(e.project!!).getDocument(psiFile).
//        val psiMethod: PsiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java)!!
//        return KtClassHelper.getKtClassForElement(psiElement)
    }
}