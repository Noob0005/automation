package com.myremote.admin.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import java.util.UUID

/**
 * Secure storage for device key and configuration using EncryptedSharedPreferences
 */
class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "myremote_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_DEVICE_KEY = "device_key"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_IS_SETUP_COMPLETE = "is_setup_complete"
        private const val KEY_LAST_PING = "last_ping"
    }

    /**
     * Generate a new 32-character unique device key
     */
    fun generateDeviceKey(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        val key = uuid.take(32).uppercase()
        saveDeviceKey(key)
        return key
    }

    /**
     * Save device key securely
     */
    fun saveDeviceKey(key: String) {
        if (key.length != 32) {
            throw IllegalArgumentException("Device key must be exactly 32 characters")
        }
        prefs.edit().putString(KEY_DEVICE_KEY, key).apply()
    }

    /**
     * Get stored device key
     */
    fun getDeviceKey(): String? {
        return prefs.getString(KEY_DEVICE_KEY, null)
    }

    /**
     * Verify if provided key matches stored key
     */
    fun verifyDeviceKey(key: String): Boolean {
        val storedKey = getDeviceKey()
        return storedKey != null && storedKey == key
    }

    /**
     * Save FCM token
     */
    fun saveFcmToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    /**
     * Get FCM token
     */
    fun getFcmToken(): String? {
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    /**
     * Mark setup as complete
     */
    fun setSetupComplete(complete: Boolean) {
        prefs.edit().putBoolean(KEY_IS_SETUP_COMPLETE, complete).apply()
    }

    /**
     * Check if setup is complete
     */
    fun isSetupComplete(): Boolean {
        return prefs.getBoolean(KEY_IS_SETUP_COMPLETE, false)
    }

    /**
     * Update last ping timestamp
     */
    fun updateLastPing() {
        prefs.edit().putLong(KEY_LAST_PING, System.currentTimeMillis()).apply()
    }

    /**
     * Get last ping timestamp
     */
    fun getLastPing(): Long {
        return prefs.getLong(KEY_LAST_PING, 0L)
    }

    /**
     * Get or create device ID
     */
    fun getOrCreateDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    /**
     * Clear all secure data (for reset)
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
