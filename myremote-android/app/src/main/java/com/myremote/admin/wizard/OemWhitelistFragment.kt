package com.myremote.admin.wizard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.myremote.admin.R

class OemWhitelistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_oem, container, false)
        
        val checkButton = view.findViewById<MaterialButton>(R.id.checkOemButton)
        
        checkButton.setOnClickListener {
            openOemSettings()
        }
        
        return view
    }

    private fun openOemSettings() {
        // Try to open manufacturer-specific battery/settings pages
        val manufacturer = android.os.Build.MANUFACTURER.lowercase()
        
        val intent = when {
            manufacturer.contains("xiaomi") -> {
                Intent("miui.intent.action.OP_AUTO_START").apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
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
            manufacturer.contains("oppo") -> {
                Intent().apply {
                    setClassName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }
            }
            manufacturer.contains("vivo") -> {
                Intent().apply {
                    setClassName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
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
                // Open general app info settings
                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
            }
        }
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to app info
            val fallback = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${requireContext().packageName}")
            }
            startActivity(fallback)
        }
    }
}
