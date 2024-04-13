package com.ylz.kt_extension_refactor.utils

import android.databinding.tool.ext.toCamelCase
import com.android.tools.idea.databinding.psiclass.LightBindingClass
import com.android.tools.idea.res.isIdDeclaration
import com.intellij.psi.xml.XmlAttributeValue
import org.jetbrains.kotlin.android.synthetic.AndroidConst
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
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
    return psiElement is XmlAttributeValue && isIdDeclaration(psiElement)
}

//fun LightBindingClass.getValName() = "${name.substring(0, 1).lowercase()}${name.substring(1)}"
//
//fun String.snakeCaseToCamelCase() = this.replace("_(\\w)".toRegex()) {
//    if (it.groupValues.size > 1)
//        it.groupValues[1].uppercase()
//    else ""
//}


fun KtClass.addingBindingVal(bindingClass: LightBindingClass) {
    findPropertyByName(bindingClass.name.toCamelCase())?.run { return }

    val project = project
    val factory = KtPsiFactory(project)

    val isActivity = name?.contains("Activity") == true
    val bindingProperty = factory.createProperty(
        "val ${bindingClass.name.toCamelCase()}: ${bindingClass.name}"
    )
    val onCreateFunction =
        findFunctionByName(if (isActivity) "onCreate" else "onCreateView")
    onCreateFunction?.let { kFunction ->
    if (isWritable)
        addBefore(bindingProperty, kFunction)
    }
}

fun KtClass.refactorExpression(expression: KtSimpleNameExpression, bindingClass: LightBindingClass) {
    val project = project
    val factory = KtPsiFactory(project)

    //todo fix logic here
//    simpleNameExpressionVisitor {
//        it.textRange
//        expression.text.toCamelCase()
//        "${bindingClass.text.toCamelCase()}.${expression.text.toCamelCase()}"
//    }
}