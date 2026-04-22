package com.myremote.admin.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStorage {

    private const val PREFS_NAME = "myremote_secure_prefs"
    
    @Volatile
    private var instance: EncryptedSharedPreferences? = null

    fun getInstance(context: Context): EncryptedSharedPreferences {
        return instance ?: synchronized(this) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            instance = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ) as EncryptedSharedPreferences
            instance!!
        }
    }

    fun saveDeviceKey(context: Context, key: String) {
        getInstance(context).edit().putString("device_key", key).apply()
    }

    fun getDeviceKey(context: Context): String? {
        return getInstance(context).getString("device_key", null)
    }

    fun hasDeviceKey(context: Context): Boolean {
        return !getDeviceKey(context).isNullOrEmpty()
    }

    fun clear(context: Context) {
        getInstance(context).edit().clear().apply()
    }
}
