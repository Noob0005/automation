package com.myremote.admin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.myremote.admin.util.DeviceKeyManager
import com.myremote.admin.wizard.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if setup is complete
        if (!DeviceKeyManager.isKeyGenerated()) {
            // Start wizard
            startActivity(android.content.Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }
        
        // TODO: Show main dashboard
        setContentView(android.widget.TextView(this).apply {
            text = "MyRemote Admin - Dashboard (Coming Soon)\nDevice Key: ${DeviceKeyManager.getDeviceKey()?.take(8)}..."
            setPadding(32, 32, 32, 32)
            textSize = 16f
        })
    }

    override fun onResume() {
        super.onResume()
        // Refresh status
    }
}
