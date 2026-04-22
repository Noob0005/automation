package com.myremote.admin.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.myremote.admin.model.RemoteCommand
import com.myremote.admin.util.DeviceKeyManager

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyRemoteFCM"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        DeviceKeyManager.saveFcmToken(token)
        // TODO: Update token in Firestore
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        message.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${message.data}")
            
            try {
                val commandJson = message.data["command"] ?: return@let
                val gson = Gson()
                val command = gson.fromJson(commandJson, RemoteCommand::class.java)
                
                Log.d(TAG, "Command received: ${command.type}")
                
                // Forward to RemoteAdminService
                RemoteAdminService.getInstance()?.executeCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing command", e)
            }
        }

        message.notification?.let {
            Log.d(TAG, "Message Notification Title: ${it.title}")
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
}
