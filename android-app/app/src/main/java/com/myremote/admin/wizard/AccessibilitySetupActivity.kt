package com.myremote.admin.wizard

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.myremote.admin.R
import com.myremote.admin.service.GestureAccessibilityService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccessibilitySetupActivity : AppCompatActivity() {

    private val accessibilityManager by lazy {
        getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_accessibility_setup, null))

        findViewById<TextView>(R.id.titleText).text = getString(R.string.accessibility_title)
        findViewById<TextView>(R.id.descText).text = getString(R.string.accessibility_desc)
        findViewById<TextView>(R.id.instructionText).text = getString(R.string.accessibility_instruction)

        findViewById<Button>(R.id.openSettingsButton).setOnClickListener {
            openAccessibilitySettings()
        }

        findViewById<Button>(R.id.checkButton).setOnClickListener {
            checkAccessibilityStatus()
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (isAccessibilityEnabled()) {
                startActivity(Intent(this, NotificationAccessActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enable accessibility service first", Toast.LENGTH_SHORT).show()
            }
        }

        // Auto-check status periodically
        lifecycleScope.launch {
            while (true) {
                delay(1000)
                if (isAccessibilityEnabled()) {
                    Toast.makeText(this@AccessibilitySetupActivity, "Accessibility enabled!", Toast.LENGTH_SHORT).show()
                    break
                }
            }
        }
    }

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )

        for (service in enabledServices) {
            val serviceInfo = service.resolveInfo.serviceInfo
            if (serviceInfo.packageName == packageName &&
                serviceInfo.name == GestureAccessibilityService::class.java.name) {
                return true
            }
        }
        return false
    }

    private fun checkAccessibilityStatus() {
        val enabled = isAccessibilityEnabled()
        val statusText = findViewById<TextView>(R.id.statusText)
        statusText.text = if (enabled) {
            getString(R.string.enabled)
        } else {
            getString(R.string.not_enabled)
        }
        statusText.setTextColor(getColor(if (enabled) R.color.success else R.color.error))
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
