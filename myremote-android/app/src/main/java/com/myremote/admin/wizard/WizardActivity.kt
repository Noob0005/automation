package com.myremote.admin.wizard

import android.Manifest
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.myremote.admin.R
import com.myremote.admin.data.DeviceKeyGenerator
import com.myremote.admin.data.SecureStorage
import com.myremote.admin.ui.MainActivity

class WizardActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var nextButton: MaterialButton
    private lateinit var skipButton: MaterialButton
    private lateinit var progressIndicator: com.google.android.material.progressindicator.LinearProgressIndicator

    private val fragments = listOf(
        WelcomeFragment(),
        PermissionsFragment(),
        AccessibilityFragment(),
        NotificationAccessFragment(),
        FileAccessFragment(),
        BatteryOptimizationFragment(),
        DeviceAdminFragment(),
        KeyGenerationFragment(),
        OemWhitelistFragment()
    )

    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        viewPager = findViewById(R.id.viewPager)
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)
        progressIndicator = findViewById(R.id.progressIndicator)

        setupViewPager()
        setupButtons()
        updateUI()
    }

    private fun setupViewPager() {
        viewPager.adapter = WizardAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentStep = position
                updateUI()
            }
        })
    }

    private fun setupButtons() {
        nextButton.setOnClickListener {
            if (currentStep < fragments.size - 1) {
                viewPager.currentItem = currentStep + 1
            } else {
                completeWizard()
            }
        }

        skipButton.setOnClickListener {
            if (currentStep < fragments.size - 1) {
                viewPager.currentItem = currentStep + 1
            }
        }
    }

    private fun updateUI() {
        progressIndicator.setProgress((currentStep + 1) * 100 / fragments.size)
        
        nextButton.text = if (currentStep == fragments.size - 1) 
            getString(R.string.wizard_complete) 
            else getString(R.string.next)
        
        skipButton.visibility = if (currentStep >= fragments.size - 2) View.GONE else View.VISIBLE
    }

    private fun completeWizard() {
        // Generate and save device key if not already done
        if (!SecureStorage.hasDeviceKey(this)) {
            val key = DeviceKeyGenerator.generate()
            SecureStorage.saveDeviceKey(this, key)
        }

        // Start main activity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Fragment permission result handlers
    fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
        )
        permissionLauncher.launch(permissions)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        val fragment = fragments[1] as PermissionsFragment
        fragment.onPermissionsResult(allGranted)
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    fun requestFileAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                fileAccessLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                fileAccessLauncher.launch(intent)
            }
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            fileAccessLauncher.launch(intent)
        }
    }

    private val fileAccessLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val fragment = fragments[4] as FileAccessFragment
        fragment.onFileAccessResult()
    }

    fun requestBatteryOptimizationExemption() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:$packageName")
        batteryLauncher.launch(intent)
    }

    private val batteryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val fragment = fragments[5] as BatteryOptimizationFragment
        fragment.onBatteryResult()
    }

    fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, AdminReceiver.getComponentName(this))
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable remote administration features")
        adminLauncher.launch(intent)
    }

    private val adminLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val fragment = fragments[6] as DeviceAdminFragment
        fragment.onAdminResult()
    }

    fun generateAndSaveKey() {
        val key = DeviceKeyGenerator.generate()
        SecureStorage.saveDeviceKey(this, key)
        val fragment = fragments[7] as KeyGenerationFragment
        fragment.onKeyGenerated(key)
    }

    companion object {
        const val REQUEST_CODE_OEM = 1001
    }
}
