package com.ylz.viewbinding_migration

import com.intellij.DynamicBundle

private const val BUNDLE = "messages.ViewBindingLintBundle"

class ViewBindingLintBundle private constructor() {
    companion object {
        private val bundle = DynamicBundle(
            ViewBindingLintBundle::class.java,
            BUNDLE
        )

        @JvmStatic
        fun message(key: String, vararg params: Any): String {
            return bundle.getMessage(key, *params)
        }
    }
}