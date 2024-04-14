package com.ylz.kt_extension_refactor

import com.android.tools.idea.databinding.module.LayoutBindingModuleCache
import com.android.tools.idea.databinding.psiclass.LightBindingClass
import com.intellij.codeInsight.daemon.QuickFixActionRegistrar
import com.intellij.codeInsight.quickfix.UnresolvedReferenceQuickFixProvider
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.ylz.kt_extension_refactor.utils.addingBindingVal
import com.ylz.kt_extension_refactor.utils.findContainerKtClass
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.codeinsight.api.classic.quickfixes.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class AddingBindingPropertyQuickfix(
    expression: KtSimpleNameExpression,
    val bindingClass: LightBindingClass
) :
    KotlinQuickFixAction<KtSimpleNameExpression>(expression) {
    override fun getFamilyName(): String = "adding.viewbinding.property"

    override fun getText(): String = "Adding viewBinding property"

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        WriteCommandAction.runWriteCommandAction(project) {
            this.element?.findContainerKtClass()?.addingBindingVal(bindingClass)
        }
    }
}

class AddingBindingPropertyQuickfixProvider :
    UnresolvedReferenceQuickFixProvider<KtSimpleNameReference>() {

    override fun registerFixes(
        reference: KtSimpleNameReference,
        registrar: QuickFixActionRegistrar
    ) {
        val facet = AndroidFacet.getInstance(reference.element) ?: return
        val cache = LayoutBindingModuleCache.getInstance(facet)
        val groups = cache.bindingLayoutGroups
        if (groups.isEmpty()) return

        val bindingClass = groups.firstNotNullOf { group ->
            cache.getLightBindingClasses(group).firstOrNull { lightBindingClass ->
                lightBindingClass.name.equals(reference.element.text, true)
            }
        }

        registrar.register(AddingBindingPropertyQuickfix(reference.expression, bindingClass))
    }

    override fun getReferenceClass(): Class<KtSimpleNameReference> {
        return KtSimpleNameReference::class.java
    }
}