package com.myremote.admin.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.myremote.admin.MyRemoteApplication
import java.security.SecureRandom

object DeviceKeyManager {
    
    private const val KEY_DEVICE_KEY = "device_key"
    private const val KEY_KEY_GENERATED = "key_generated"
    private const val KEY_FCM_TOKEN = "fcm_token"
    private const val KEY_LAST_PING = "last_ping"
    
    fun getEncryptedPrefs(): EncryptedSharedPreferences {
        return MyRemoteApplication.instance.getEncryptedPrefs()
    }
    
    fun generateDeviceKey(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = SecureRandom()
        return StringBuilder(32).apply {
            repeat(32) {
                append(chars[random.nextInt(chars.length)])
            }
        }.toString()
    }
    
    fun saveDeviceKey(key: String) {
        getEncryptedPrefs().edit().apply {
            putString(KEY_DEVICE_KEY, key)
            putBoolean(KEY_KEY_GENERATED, true)
            apply()
        }
    }
    
    fun getDeviceKey(): String? {
        return getEncryptedPrefs().getString(KEY_DEVICE_KEY, null)
    }
    
    fun isKeyGenerated(): Boolean {
        return getEncryptedPrefs().getBoolean(KEY_KEY_GENERATED, false)
    }
    
    fun saveFcmToken(token: String) {
        getEncryptedPrefs().edit().apply {
            putString(KEY_FCM_TOKEN, token)
            apply()
        }
    }
    
    fun getFcmToken(): String? {
        return getEncryptedPrefs().getString(KEY_FCM_TOKEN, null)
    }
    
    fun updateLastPing() {
        getEncryptedPrefs().edit().apply {
            putLong(KEY_LAST_PING, System.currentTimeMillis())
            apply()
        }
    }
    
    fun getLastPing(): Long {
        return getEncryptedPrefs().getLong(KEY_LAST_PING, 0L)
    }
    
    fun resetAll() {
        getEncryptedPrefs().edit().clear().apply()
    }
}
