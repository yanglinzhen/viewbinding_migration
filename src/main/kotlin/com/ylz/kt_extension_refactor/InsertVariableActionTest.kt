package com.ylz.kt_extension_refactor

import android.databinding.tool.ext.capitalizeUS
import android.databinding.tool.ext.decapitalizeUS
import android.databinding.tool.ext.toCamelCase
import junit.framework.TestCase.assertEquals
import org.junit.Test

class InsertVariableActionTest {

    @Test
    fun toCamelCaseValTest() {
        val mapping = mapOf(
            "activity_main" to "activityMain",
            "tv_hello_world" to "tvHelloWorld",
            "iv_icon" to "ivIcon",
            "bt_button" to "btButton"
        )

        mapping.forEach { (t, u) ->
            val result = t.toCamelCase().decapitalizeUS()
            assertEquals(u, result)
        }
    }

    @Test
    fun findBindingClassByNameTest() {
        val mapping = mapOf(
            "activityMainBinding" to "ActivityMainBinding",
            "mmLayoutSummaryListErrorBinding" to "MmLayoutSummaryListErrorBinding",
            "pocMmBtDoubleActionBinding" to "PocMmBtDoubleActionBinding",
        )

        mapping.forEach { (t, u) ->
            val result = t.toCamelCase().capitalizeUS()
            assertEquals(u, result)
        }
    }
}