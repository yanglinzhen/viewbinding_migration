package com.ylz.viewbinding_migration.utils

import android.databinding.tool.ext.decapitalizeUS
import com.android.tools.idea.databinding.module.LayoutBindingModuleCache
import com.android.tools.idea.databinding.psiclass.LightBindingClass
import com.android.tools.idea.res.isIdDeclaration
import com.ibm.icu.lang.UCharacter.LineBreak
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttributeValue
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import org.jetbrains.kotlin.resolve.source.getPsi

fun KtSimpleNameExpression.findBindingClassByExpression(
    facet: AndroidFacet,
    editor: Editor
): LightBindingClass? {
    val layoutName = GotoDeclarationAction.findTargetElement(
        editor.project,
        editor,
        textOffset
    )?.containingFile?.virtualFile?.nameWithoutExtension ?: return null

    return LayoutBindingModuleCache.getInstance(facet).let { cache ->
        cache.bindingLayoutGroups.firstOrNull {
            layoutName == it.mainLayout.file.nameWithoutExtension
        }?.let {
            cache.getLightBindingClasses(it).first()
        }
    }
}

fun PsiElement.findContainerKtClass() = PsiTreeUtil.getParentOfType(
    this,
    KtClass::class.java
)

fun KtReferenceExpression.getTargetPropertyDescriptor(): PropertyDescriptor? =
    resolveToCall()?.resultingDescriptor as? PropertyDescriptor

fun KtSimpleNameExpression.isIdReference(): Boolean {
    val psiElement = getTargetPropertyDescriptor()?.source?.getPsi() ?: false
    return psiElement is XmlAttributeValue && isIdDeclaration(psiElement)
}

fun KtClass.addingBindingVal(bindingClass: LightBindingClass) {

    findPropertyByName(bindingClass.name.decapitalizeUS())?.run { return }

    val project = project
    val factory = KtPsiFactory(project)

    val isActivity = name?.contains("Activity") == true
    val privateBindingProperty = factory.createProperty(
        "private var _${bindingClass.name.decapitalizeUS()}: ${bindingClass.name}? = null"
    )
    val protectedBindingProperty = factory.createProperty(
        "protected val ${bindingClass.name.decapitalizeUS()}"
    )
    val protectedBindingPropertyGetter = factory.createPropertyGetter(
        factory.createExpression(
            "${privateBindingProperty.name ?: ""}!!"
        )
    )

    val onCreateFunction =
        findFunctionByName(if (isActivity) "onCreate" else "onCreateView")
    onCreateFunction?.let { kFunction ->
        if (isWritable) {
            addBefore(privateBindingProperty, kFunction)
            addBefore(protectedBindingProperty, kFunction)
            addBefore(protectedBindingPropertyGetter, kFunction)
            addBefore(factory.createNewLine(LineBreak.BREAK_AFTER), kFunction)
        }
    }
}