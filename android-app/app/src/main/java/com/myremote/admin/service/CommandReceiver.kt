package com.myremote.admin.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.myremote.admin.model.RemoteCommand

class CommandReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "CommandReceiver"
        const val ACTION_COMMAND = "com.myremote.admin.ACTION_COMMAND"
        const val EXTRA_COMMAND_JSON = "command_json"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_COMMAND) {
            val commandJson = intent.getStringExtra(EXTRA_COMMAND_JSON) ?: return
            
            try {
                val gson = Gson()
                val command = gson.fromJson(commandJson, RemoteCommand::class.java)
                
                Log.d(TAG, "Command received via broadcast: ${command.type}")
                
                // Forward to service
                RemoteAdminService.getInstance()?.executeCommand(command)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing command", e)
            }
        }
    }
}
