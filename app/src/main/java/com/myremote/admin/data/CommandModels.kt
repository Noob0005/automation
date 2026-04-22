package com.myremote.admin.data

import com.google.gson.annotations.SerializedName

/**
 * Command types supported by the remote admin system
 */
enum class CommandType(val value: String) {
    @SerializedName("ping")
    PING("ping"),
    
    @SerializedName("location")
    LOCATION("location"),
    
    @SerializedName("photo")
    PHOTO("photo"),
    
    @SerializedName("audio")
    AUDIO("audio"),
    
    @SerializedName("screen_share_start")
    SCREEN_SHARE_START("screen_share_start"),
    
    @SerializedName("screen_share_stop")
    SCREEN_SHARE_STOP("screen_share_stop"),
    
    @SerializedName("tap")
    TAP("tap"),
    
    @SerializedName("swipe")
    SWIPE("swipe"),
    
    @SerializedName("type")
    TYPE("type"),
    
    @SerializedName("toggle_wifi")
    TOGGLE_WIFI("toggle_wifi"),
    
    @SerializedName("toggle_bluetooth")
    TOGGLE_BLUETOOTH("toggle_bluetooth"),
    
    @SerializedName("toggle_location")
    TOGGLE_LOCATION("toggle_location"),
    
    @SerializedName("lock_device")
    LOCK_DEVICE("lock_device"),
    
    @SerializedName("ring_device")
    RING_DEVICE("ring_device");
    
    companion object {
        fun fromValue(value: String): CommandType? {
            return values().find { it.value == value }
        }
    }
}

/**
 * Command data class for FCM messages
 */
data class RemoteCommand(
    @SerializedName("command_id")
    val commandId: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("device_key")
    val deviceKey: String,
    
    @SerializedName("timestamp")
    val timestamp: Long,
    
    @SerializedName("params")
    val params: Map<String, Any>? = null,
    
    @SerializedName("ttl")
    val ttl: Int = 300 // 5 minutes default
)

/**
 * Command response data class
 */
data class CommandResponse(
    @SerializedName("command_id")
    val commandId: String,
    
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("result")
    val result: Any? = null,
    
    @SerializedName("error")
    val error: String? = null,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Location data class
 */
data class LocationData(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("accuracy")
    val accuracy: Float?,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Device status data class
 */
data class DeviceStatus(
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("online")
    val online: Boolean,
    
    @SerializedName("last_seen")
    val lastSeen: Long,
    
    @SerializedName("battery_level")
    val batteryLevel: Int?,
    
    @SerializedName("is_charging")
    val isCharging: Boolean?,
    
    @SerializedName("wifi_enabled")
    val wifiEnabled: Boolean?,
    
    @SerializedName("location_enabled")
    val locationEnabled: Boolean?
)
