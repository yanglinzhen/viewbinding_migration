package com.ylz.kt_extension_refactor

import ai.grazie.utils.capitalize
import org.junit.Assert.*
import org.junit.Test

class InsertVariableActionTest {

    @Test
    fun renameTest() {
        val mapping = mapOf(
            "activity_main" to "activityMain",
            "tv_hello_world" to "tvHelloWorld",
            "iv_icon" to "ivIcon",
            "bt_button" to "btButton"
        )

        mapping.forEach { (t, u) ->
            val result = t.replace("_(\\w)".toRegex()) {
                it.groupValues[1].uppercase()
            }
            println(result)
//            println(t.replace("_(\\w)(\\w*)".toRegex(), "${"$1".uppercase()}${"$2"}"))
        }
    }
}