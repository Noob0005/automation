package com.myremote.admin.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.myremote.admin.R
import com.myremote.admin.model.CommandPayload
import com.myremote.admin.model.CommandType
import com.myremote.admin.model.CommandResult
import com.myremote.admin.model.DeviceInfo
import com.myremote.admin.ui.MainActivity
import com.myremote.admin.util.DeviceKeyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class RemoteAdminService : Service() {

    companion object {
        private const val TAG = "RemoteAdminService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "remote_admin_channel"
        
        private var instance: RemoteAdminService? = null
        val isRunning: StateFlow<Boolean> = MutableStateFlow(false)
        
        fun getInstance(): RemoteAdminService? = instance
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val db = FirebaseFirestore.getInstance()
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        (isRunning as MutableStateFlow).value = true
        acquireWakeLock()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d(TAG, "Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MyRemote Admin")
            .setContentText("Remote administration service running")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyRemoteAdmin::WakeLock"
        ).apply {
            acquire(10*60*1000L) // 10 minutes max
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
    }

    fun executeCommand(command: com.myremote.admin.model.RemoteCommand) {
        serviceScope.launch {
            try {
                // Verify device key
                val storedKey = DeviceKeyManager.getDeviceKey()
                if (storedKey != command.deviceKey) {
                    sendResult(command.commandId, false, mapOf(), "Invalid device key")
                    return@launch
                }

                // Check command timestamp (5 min expiry)
                val now = System.currentTimeMillis()
                if (now - command.timestamp > 5 * 60 * 1000) {
                    sendResult(command.commandId, false, mapOf(), "Command expired")
                    return@launch
                }

                Log.d(TAG, "Executing command: ${command.type}")

                when (command.type) {
                    CommandType.PING -> handlePing(command.commandId)
                    CommandType.GET_LOCATION -> handleLocation(command.commandId)
                    CommandType.TAKE_PHOTO -> handlePhoto(command.commandId)
                    CommandType.RECORD_AUDIO -> handleAudio(command.commandId)
                    CommandType.START_SCREEN_SHARE -> handleStartScreenShare(command.commandId, command.payload)
                    CommandType.STOP_SCREEN_SHARE -> handleStopScreenShare(command.commandId)
                    CommandType.TAP -> handleTap(command.commandId, command.payload)
                    CommandType.SWIPE -> handleSwipe(command.commandId, command.payload)
                    CommandType.TYPE_TEXT -> handleTypeText(command.commandId, command.payload)
                    CommandType.PRESS_BACK -> handleBack(command.commandId)
                    CommandType.PRESS_HOME -> handleHome(command.commandId)
                    CommandType.TOGGLE_WIFI -> handleToggleWifi(command.commandId)
                    CommandType.TOGGLE_BLUETOOTH -> handleToggleBluetooth(command.commandId)
                    CommandType.TOGGLE_FLASHLIGHT -> handleToggleFlashlight(command.commandId)
                    CommandType.GET_DEVICE_INFO -> handleDeviceInfo(command.commandId)
                    else -> sendResult(command.commandId, false, mapOf(), "Unknown command")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Command execution error", e)
                sendResult(command.commandId, false, mapOf(), e.message ?: "Execution error")
            }
        }
    }

    private suspend fun handlePing(commandId: String) {
        DeviceKeyManager.updateLastPing()
        val data = mapOf(
            "status" to "online",
            "timestamp" to System.currentTimeMillis(),
            "keyGenerated" to DeviceKeyManager.isKeyGenerated()
        )
        sendResult(commandId, true, data)
    }

    private suspend fun handleLocation(commandId: String) {
        val location = getLastKnownLocation()
        val data = if (location != null) {
            mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "accuracy" to location.accuracy,
                "time" to location.time
            )
        } else {
            mapOf("error" to "Location unavailable")
        }
        sendResult(commandId, location != null, data)
    }

    private suspend fun handlePhoto(commandId: String) {
        // Photo capture implementation
        val data = mapOf("status" to "pending", "message" to "Photo capture not yet implemented")
        sendResult(commandId, false, data)
    }

    private suspend fun handleAudio(commandId: String) {
        // Audio recording implementation
        val data = mapOf("status" to "pending", "message" to "Audio recording not yet implemented")
        sendResult(commandId, false, data)
    }

    private suspend fun handleStartScreenShare(commandId: String, payload: CommandPayload?) {
        // WebRTC screen share start
        val data = mapOf("status" to "pending", "message" to "Screen share not yet implemented")
        sendResult(commandId, false, data)
    }

    private suspend fun handleStopScreenShare(commandId: String) {
        val data = mapOf("status" to "stopped")
        sendResult(commandId, true, data)
    }

    private suspend fun handleTap(commandId: String, payload: CommandPayload?) {
        val x = payload?.x ?: 0
        val y = payload?.y ?: 0
        val success = GestureAccessibilityService.getInstance()?.performTap(x, y) ?: false
        val data = mapOf("x" to x, "y" to y, "success" to success)
        sendResult(commandId, success, data)
    }

    private suspend fun handleSwipe(commandId: String, payload: CommandPayload?) {
        val startX = payload?.x ?: 0
        val startY = payload?.y ?: 0
        val direction = payload?.direction ?: "right"
        val duration = payload?.duration ?: 300L
        
        val (endX, endY) = when (direction) {
            "up" -> Pair(startX, startY - 300)
            "down" -> Pair(startX, startY + 300)
            "left" -> Pair(startX - 300, startY)
            "right" -> Pair(startX + 300, startY)
            else -> Pair(startX + 300, startY)
        }
        
        val success = GestureAccessibilityService.getInstance()?.performSwipe(
            startX, startY, endX, endY, duration
        ) ?: false
        
        val data = mapOf("startX" to startX, "startY" to startY, "direction" to direction, "success" to success)
        sendResult(commandId, success, data)
    }

    private suspend fun handleTypeText(commandId: String, payload: CommandPayload?) {
        val text = payload?.text ?: ""
        val success = GestureAccessibilityService.getInstance()?.typeText(text) ?: false
        val data = mapOf("text" to text, "success" to success)
        sendResult(commandId, success, data)
    }

    private suspend fun handleBack(commandId: String) {
        val success = GestureAccessibilityService.getInstance()?.performBack() ?: false
        sendResult(commandId, success, mapOf("action" to "back", "success" to success))
    }

    private suspend fun handleHome(commandId: String) {
        val success = GestureAccessibilityService.getInstance()?.performHome() ?: false
        sendResult(commandId, success, mapOf("action" to "home", "success" to success))
    }

    private suspend fun handleToggleWifi(commandId: String) {
        val wifiManager = applicationContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wasEnabled = wifiManager.isWifiEnabled
        wifiManager.isWifiEnabled = !wasEnabled
        val data = mapOf("previous_state" to wasEnabled, "new_state" to !wasEnabled)
        sendResult(commandId, true, data)
    }

    private suspend fun handleToggleBluetooth(commandId: String) {
        val data = mapOf("status" to "pending", "message" to "Bluetooth toggle requires additional permissions")
        sendResult(commandId, false, data)
    }

    private suspend fun handleToggleFlashlight(commandId: String) {
        val data = mapOf("status" to "pending", "message" to "Flashlight toggle not yet implemented")
        sendResult(commandId, false, data)
    }

    private suspend fun handleDeviceInfo(commandId: String) {
        val deviceInfo = getDeviceInfo()
        sendResult(commandId, true, mapOf(
            "deviceId" to deviceInfo.deviceId,
            "model" to deviceInfo.model,
            "manufacturer" to deviceInfo.manufacturer,
            "androidVersion" to deviceInfo.androidVersion,
            "sdkLevel" to deviceInfo.sdkLevel,
            "batteryLevel" to deviceInfo.batteryLevel,
            "isCharging" to deviceInfo.isCharging,
            "networkType" to deviceInfo.networkType,
            "lastSeen" to System.currentTimeMillis()
        ))
    }

    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            deviceId = Build.SERIAL.takeIf { it != "unknown" } ?: UUID.randomUUID().toString(),
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            androidVersion = Build.VERSION.RELEASE,
            sdkLevel = Build.VERSION.SDK_INT,
            batteryLevel = 0, // Implement battery receiver
            isCharging = false,
            networkType = "unknown",
            lastSeen = System.currentTimeMillis()
        )
    }

    private fun getLastKnownLocation(): Location? {
        // Implement location retrieval
        return null
    }

    private suspend fun sendResult(commandId: String, success: Boolean, data: Map<String, Any?>, error: String? = null) {
        val result = CommandResult(commandId, success, data, error)
        
        // Upload result to Firestore
        try {
            db.collection("command_results")
                .document(commandId)
                .set(mapOf(
                    "commandId" to result.commandId,
                    "success" to result.success,
                    "data" to result.data,
                    "error" to (result.error ?: ""),
                    "timestamp" to result.timestamp
                ))
                .await()
            Log.d(TAG, "Result sent for command: $commandId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send result", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        (isRunning as MutableStateFlow).value = false
        releaseWakeLock()
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Restart service if needed
        val restartIntent = Intent(applicationContext, this.javaClass)
        startService(restartIntent)
        super.onTaskRemoved(rootIntent)
    }
}

// Extension function for await
suspend fun com.google.android.gms.tasks.Task<Void>.await() {
    return kotlin.coroutines.suspendCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(Unit) }
        addOnFailureListener { continuation.resumeWith(Result.failure(it)) }
    }
}
