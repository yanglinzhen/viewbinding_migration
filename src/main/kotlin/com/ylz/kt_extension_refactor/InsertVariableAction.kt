package com.ylz.kt_extension_refactor

import com.android.tools.idea.databinding.module.LayoutBindingModuleCache
import com.android.tools.idea.databinding.psiclass.LightBindingClass
import com.android.tools.idea.res.ModuleRClass
import com.android.tools.idea.res.findIdUrlsInFile
import com.android.tools.idea.res.psi.AndroidResourceToPsiResolver
import com.android.tools.idea.res.psi.ResourceReferencePsiElement
import com.android.tools.idea.util.androidFacet
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.LocalSearchScope
import com.intellij.usages.UsageTargetUtil
import com.ylz.kt_extension_refactor.utils.getVariableName
import com.ylz.kt_extension_refactor.utils.isIdReference
import com.ylz.kt_extension_refactor.utils.isLayoutReference
import com.ylz.kt_extension_refactor.utils.snakeCaseToCamelCase
import org.jetbrains.android.augment.ResourceLightField
import org.jetbrains.android.augment.ResourceRepositoryInnerRClass
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtValueArgumentList

class InsertVariableAction : AnAction() {

    private fun doRefactoring(file: PsiFile, project: Project, editor: Editor) {

        LocalSearchScope(file).scope.filterIsInstance(KtElement::class.java).forEach {
            it.accept(object : KtTreeVisitorVoid() {

                override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {

                    val isLayoutReference = expression.isLayoutReference()
                    val isIdReference = expression.isIdReference()

                    when {
                        isIdReference -> {
                            GotoDeclarationAction.findTargetElement(
                                project,
                                editor,
                                expression.textOffset
                            )?.let { element ->
                                element.containingFile.virtualFile.nameWithoutExtension
                            }?.let { layoutName ->
                                findBindingClass(layoutName, file.androidFacet!!)
                            }?.let { bindingClass ->
                                renameIdReference(
                                    expression.textRange,
                                    expression.text,
                                    project,
                                    editor.document,
                                    bindingClass.getVariableName()
                                )
                            }
                        }

                        isLayoutReference -> {
                            expression.text
                        }
                    }
                    super.visitSimpleNameExpression(expression)
                }
            })
        }
    }

    private fun getPackageFileAndIds(
        layoutRef: PsiElement
    ): Triple<String, String, List<String>> =
        UsageTargetUtil.findUsageTargets(layoutRef).firstOrNull()?.let { target ->
            (target as? PsiElement2UsageTargetAdapter)?.element as? ResourceReferencePsiElement
        }?.let { element ->
            val delegate = element.delegate
            if (delegate is ResourceLightField) {
                ((delegate.containingClass as ResourceRepositoryInnerRClass).parent as ModuleRClass).packageName
                val packageName =
                    (((element.delegate as ResourceLightField).containingClass as ResourceRepositoryInnerRClass).parent as ModuleRClass).packageName!!
                val fileName = element.name!!
                val layoutFile =
                    AndroidResourceToPsiResolver.getInstance().getGotoDeclarationFileBasedTargets(
                        element.resourceReference,
                        element
                    ).firstOrNull()
                layoutFile?.let { file ->
                    Triple(packageName, fileName, file)
                }
            } else {
                null
            }
        }?.let {
            Triple(it.first, it.second, findIdUrlsInFile(it.third).map { url -> url.name }.toList())
        } ?: Triple("", "", emptyList())

    fun renameIdReference(
        textRange: TextRange,
        originalName: String,
        project: Project,
        document: Document,
        bindingVariableName: String
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            document.replaceString(
                textRange.startOffset,
                textRange.endOffset,
                "$bindingVariableName.${originalName.snakeCaseToCamelCase()}"
            )
        }
    }

    //not used for now
    private fun renameIdReference(
        file: PsiFile,
        document: Document,
        project: Project,
        nameToBeReplaced: String
    ) {
        LocalSearchScope(file).scope.filterIsInstance(KtElement::class.java).forEach {
            it.accept(object : KtTreeVisitorVoid() {
                override fun visitValueArgumentList(list: KtValueArgumentList) {
                    list.arguments.firstOrNull { arg ->
                        arg.text.contains(nameToBeReplaced)
                    }?.let { arg ->
                        WriteCommandAction.runWriteCommandAction(project) {
                            val (originalName, textRange) = arg.text to arg.textRange
                            document.replaceString(
                                textRange.startOffset,
                                textRange.endOffset,
                                "binding.${nameToBeReplaced.snakeCaseToCamelCase()}"
                            )
                        }
                    }
                }
            })
        }
    }

    private fun findBindingClass(layoutName: String, facet: AndroidFacet): LightBindingClass? {
        return LayoutBindingModuleCache.getInstance(facet).let { cache ->
            cache.bindingLayoutGroups.firstOrNull {
                layoutName == it.mainLayout.file.nameWithoutExtension
            }?.let {
                cache.getLightBindingClasses(it).first()
            }
        }
    }

    override fun actionPerformed(action: AnActionEvent) {
        val currentFile = action.getData(LangDataKeys.PSI_FILE) ?: return
        val editor = action.getData(LangDataKeys.EDITOR) ?: return

//        val layoutRef = action.getData(LangDataKeys.PSI_ELEMENT)
        val project = action.project ?: return

        doRefactoring(currentFile, project, editor)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
    }
}