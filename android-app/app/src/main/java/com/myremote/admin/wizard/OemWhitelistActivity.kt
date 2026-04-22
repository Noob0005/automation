package com.myremote.admin.wizard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myremote.admin.R
import com.myremote.admin.service.RemoteAdminService
import com.myremote.admin.ui.MainActivity

class OemWhitelistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_oem_whitelist, null))

        findViewById<TextView>(R.id.titleText).text = getString(R.string.oem_whitelist_title)
        findViewById<TextView>(R.id.descText).text = getString(R.string.oem_whitelist_desc)
        findViewById<TextView>(R.id.instructionText).text = getString(R.string.oem_instruction)

        findViewById<Button>(R.id.openSettingsButton).setOnClickListener {
            openManufacturerSettings()
        }

        findViewById<Button>(R.id.finishButton).setOnClickListener {
            // Start the service and complete setup
            startRemoteService()
            finishSetup()
        }
    }

    private fun openManufacturerSettings() {
        val manufacturer = Build.MANUFACTURER.lowercase()
        
        val intent = when {
            manufacturer.contains("xiaomi") -> {
                Intent().apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }
            }
            manufacturer.contains("huawei") -> {
                Intent().apply {
                    setClassName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                    )
                }
            }
            manufacturer.contains("oneplus") -> {
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
            }
            manufacturer.contains("samsung") -> {
                Intent().apply {
                    setClassName(
                        "com.samsung.android.lool",
                        "com.samsung.android.sm.ui.battery.BatteryActivity"
                    )
                }
            }
            else -> {
                Intent(Settings.ACTION_SETTINGS)
            }
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun startRemoteService() {
        val serviceIntent = Intent(this, RemoteAdminService::class.java)
        startForegroundService(serviceIntent)
    }

    private fun finishSetup() {
        // Clear wizard flag and go to main activity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
