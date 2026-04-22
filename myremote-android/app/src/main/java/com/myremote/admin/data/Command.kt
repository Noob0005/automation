package com.myremote.admin.data

data class Command(
    val id: String,
    val type: CommandType,
    val deviceKey: String,
    val timestamp: Long,
    val payload: Map<String, Any>? = null,
    val expiry: Long = timestamp + 300000 // 5 minutes
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiry
}

enum class CommandType {
    PING,
    GET_LOCATION,
    TAKE_PHOTO,
    RECORD_AUDIO,
    START_SCREEN_SHARE,
    STOP_SCREEN_SHARE,
    TAP,
    SWIPE,
    TYPE_TEXT,
    PRESS_BACK,
    PRESS_HOME,
    PRESS_RECENT,
    TOGGLE_WIFI,
    TOGGLE_BLUETOOTH,
    TOGGLE_FLASHLIGHT,
    GET_FILES,
    DOWNLOAD_FILE,
    GET_DEVICE_INFO
}
