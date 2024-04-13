package com.ylz.kt_extension_refactor.utils

import com.android.tools.idea.databinding.finders.BindingClassFinder
import com.android.tools.idea.databinding.psiclass.LightBindingClass
import com.android.tools.idea.res.isIdDeclaration
import com.android.tools.idea.res.psi.ResourceReferencePsiElement
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference
import com.intellij.psi.xml.XmlAttributeValue
import org.jetbrains.android.augment.AndroidLightField
import org.jetbrains.kotlin.android.synthetic.AndroidConst
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.source.getPsi

fun KtReferenceExpression.getTargetPropertyDescriptor(): PropertyDescriptor? =
    resolveToCall()?.resultingDescriptor as? PropertyDescriptor

fun isLayoutPackageIdentifier(reference: KtSimpleNameReference): Boolean {
    val probablyVariant = reference.element.parent as? KtDotQualifiedExpression ?: return false
    val probablyKAS =
        probablyVariant.receiverExpression as? KtDotQualifiedExpression ?: return false
    return probablyKAS.receiverExpression.text == AndroidConst.SYNTHETIC_PACKAGE
}

fun KtSimpleNameExpression.isLayoutReference(): Boolean {
    val reference = mainReference
    return isLayoutPackageIdentifier(reference)
}

fun KtSimpleNameExpression.isIdReference(): Boolean {
    val psiElement = getTargetPropertyDescriptor()?.source?.getPsi() ?: false
    return psiElement is AndroidLightField
            || psiElement is XmlAttributeValue && isIdDeclaration(psiElement)
}

fun LightBindingClass.getVariableName() = "${name.substring(0, 1).lowercase()}${name.substring(1)}"

fun String.snakeCaseToCamelCase() = this.replace("_(\\w)".toRegex()) {
    if (it.groupValues.size > 1)
        it.groupValues[1].uppercase()
    else ""
}