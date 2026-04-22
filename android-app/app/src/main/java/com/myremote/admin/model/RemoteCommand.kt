package com.myremote.admin.model

data class RemoteCommand(
    val commandId: String,
    val type: CommandType,
    val deviceKey: String,
    val timestamp: Long,
    val payload: CommandPayload? = null
)

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
    TOGGLE_WIFI,
    TOGGLE_BLUETOOTH,
    TOGGLE_FLASHLIGHT,
    GET_FILES,
    DOWNLOAD_FILE,
    GET_DEVICE_INFO
}

data class CommandPayload(
    val x: Int? = null,
    val y: Int? = null,
    val text: String? = null,
    val duration: Long? = null,
    val direction: String? = null,
    val filePath: String? = null,
    val sdpOffer: String? = null,
    val iceCandidate: String? = null
)

data class CommandResult(
    val commandId: String,
    val success: Boolean,
    val data: Map<String, Any?>,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class DeviceInfo(
    val deviceId: String,
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val sdkLevel: Int,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val networkType: String,
    val lastSeen: Long
)
