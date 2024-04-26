package com.ylz.viewbinding_migration.lint.inspections

import com.intellij.codeInspection.CleanupLocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.ylz.viewbinding_migration.ViewBindingLintBundle
import com.ylz.viewbinding_migration.utils.findContainerKtClass
import org.jetbrains.annotations.Nls
import org.jetbrains.kotlin.idea.base.utils.fqname.fqName
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.codeinsight.api.classic.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.intentions.loopToCallChain.hasWriteUsages
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName

class ViewBindingNotInitInspection : AbstractKotlinInspection(), CleanupLocalInspectionTool {

    val LOG = Logger.getInstance(ViewBindingNotInitInspection::class.java)

    @Nls
    override fun getGroupDisplayName() =
        ViewBindingLintBundle.message("view.binding.migration.group.name")

    @Nls
    override fun getDisplayName() =
        ViewBindingLintBundle.message("view.binding.migration.not.init.name")

    override fun getShortName() = "ViewBindingNotInit"

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return object : KtVisitorVoid() {

            override fun visitProperty(property: KtProperty) {

                val fqName = property.resolveToDescriptorIfAny()?.type?.fqName ?: return
                val isBindingClass = fqName.shortName().asString().endsWith("Binding")
                        && fqName.parent().shortName().asString() == "databinding"

                if (!property.hasWriteUsages()
                    && property.isVar
                    && isBindingClass
                ) {
                    holder.registerProblem(
                        property,
                        "${property.name ?: ""} not initialized, DANGEROUS!!!!!!!!"
//                        ViewBindingInitialQuickFix(property.name ?: "")
                    )
                }
            }
        }
    }

    class ViewBindingInitialQuickFix(
        private val property: String
    ) : LocalQuickFix {
        override fun getFamilyName() = "Initialize $property"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val property = descriptor.psiElement as? KtProperty ?: return

            property.findContainerKtClass()?.apply {
                val onCreateFunction =
                    findFunctionByName("onCreate")
            }
            val ktFunction = PsiTreeUtil.getParentOfType(property, KtFunction::class.java)
        }

    }
}