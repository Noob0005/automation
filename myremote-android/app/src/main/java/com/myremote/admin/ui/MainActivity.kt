package com.myremote.admin.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.myremote.admin.data.DeviceKeyGenerator
import com.myremote.admin.data.SecureStorage
import com.myremote.admin.databinding.ActivityMainBinding
import com.myremote.admin.service.AdminService
import com.myremote.admin.wizard.WizardActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkSetupStatus()
        setupClickListeners()
        updateStatus()
    }

    private fun checkSetupStatus() {
        if (!SecureStorage.hasDeviceKey(this)) {
            startActivity(Intent(this, WizardActivity::class.java))
            finish()
            return
        }

        val deviceKey = SecureStorage.getDeviceKey(this)
        binding.deviceKeyText.text = formatDeviceKey(deviceKey!!)
        binding.deviceKeyContainer.visibility = View.VISIBLE
    }

    private fun setupClickListeners() {
        binding.copyKeyButton.setOnClickListener {
            copyDeviceKey()
        }

        binding.startServiceButton.setOnClickListener {
            startAdminService()
        }

        binding.stopServiceButton.setOnClickListener {
            stopAdminService()
        }

        binding.runWizardAgainButton.setOnClickListener {
            startActivity(Intent(this, WizardActivity::class.java))
        }
    }

    private fun startAdminService() {
        val intent = Intent(this, AdminService::class.java)
        startForegroundService(intent)
        Snackbar.make(binding.root, "Service started", Snackbar.LENGTH_SHORT).show()
        updateStatus()
    }

    private fun stopAdminService() {
        val intent = Intent(this, AdminService::class.java)
        stopService(intent)
        Snackbar.make(binding.root, "Service stopped", Snackbar.LENGTH_SHORT).show()
        updateStatus()
    }

    private fun updateStatus() {
        // Check if service is running
        val isRunning = AdminService.isRunning
        binding.statusChip.text = if (isRunning) getString(com.myremote.admin.R.string.status_service_running) 
                                  else getString(com.myremote.admin.R.string.status_service_stopped)
        binding.statusChip.setChipBackgroundColorResource(
            if (isRunning) com.myremote.admin.R.color.success else com.myremote.admin.R.color.error
        )
    }

    private fun copyDeviceKey() {
        val deviceKey = SecureStorage.getDeviceKey(this) ?: return
        
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Device Key", deviceKey)
        clipboard.setPrimaryClip(clip)
        
        Snackbar.make(binding.root, getString(com.myremote.admin.R.string.device_key_copied), Snackbar.LENGTH_SHORT).show()
    }

    private fun formatDeviceKey(key: String): String {
        // Format as 8 groups of 4 characters
        return key.chunked(4).joinToString("-")
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }
}
