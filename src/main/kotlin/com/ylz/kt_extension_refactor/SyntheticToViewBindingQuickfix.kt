package com.ylz.kt_extension_refactor

import android.databinding.tool.ext.decapitalizeUS
import android.databinding.tool.ext.toCamelCaseAsVar
import com.android.tools.idea.util.androidFacet
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.ylz.kt_extension_refactor.utils.findBindingClassByExpression
import com.ylz.kt_extension_refactor.utils.findContainerKtClass
import com.ylz.kt_extension_refactor.utils.isIdReference
import org.jetbrains.kotlin.idea.codeinsight.api.classic.quickfixes.KotlinQuickFixAction
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class SyntheticToViewBindingQuickfix(expression: KtSimpleNameExpression) :
    KotlinQuickFixAction<KtSimpleNameExpression>(expression) {
    override fun getFamilyName(): String = "replace.kotlin.synthetic.to.viewbinding"

    override fun getText(): String = "replace kotlin synthetic reference to viewBinding"

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        element?.let {
            it.findContainerKtClass() to (it to it.findBindingClassByExpression(
                file.androidFacet!!,
                editor!!
            ))
        }?.let {
            val (expression, bindingClass) = it.second
            renameIdReference(
                expression.textRange,
                expression.text,
                project,
                editor?.document!!,
                bindingClass?.name?.decapitalizeUS() ?: ""
            )
        }
    }

    private fun renameIdReference(
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
                "$bindingVariableName.${originalName.toCamelCaseAsVar()}"
            )
        }
    }
}

class SyntheticQuickfixAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val tobeFixedExpression = element as? KtSimpleNameExpression ?: return
        if (tobeFixedExpression.isIdReference()) {
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "Replace kotlin synthetic resource reference to viewBinding"
            )
                .highlightType(ProblemHighlightType.WARNING)
                .withFix(SyntheticToViewBindingQuickfix(element))
                .create()
        }
    }
}