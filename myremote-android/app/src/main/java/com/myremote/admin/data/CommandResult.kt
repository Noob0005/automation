package com.myremote.admin.data

data class CommandResult(
    val commandId: String,
    val success: Boolean,
    val data: Map<String, Any>? = null,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
