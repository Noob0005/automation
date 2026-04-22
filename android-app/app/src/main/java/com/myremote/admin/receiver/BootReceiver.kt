package com.myremote.admin.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.myremote.admin.service.RemoteAdminService

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            Log.d(TAG, "Boot completed, starting service")
            
            val serviceIntent = Intent(context, RemoteAdminService::class.java)
            try {
                context.startForegroundService(serviceIntent)
                Log.d(TAG, "Service started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start service", e)
            }
        }
    }
}
