package com.myremote.admin.wizard

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.myremote.admin.R

class DeviceAdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_admin, container, false)
        
        val activateButton = view.findViewById<MaterialButton>(R.id.activateAdminButton)
        val statusText = view.findViewById<MaterialTextView>(R.id.adminStatusText)
        
        checkAdminStatus(statusText)
        
        activateButton.setOnClickListener {
            (activity as? WizardActivity)?.requestDeviceAdmin()
        }
        
        return view
    }

    private fun checkAdminStatus(statusText: MaterialTextView) {
        val dpm = ContextCompat.getSystemService(requireContext(), DevicePolicyManager::class.java)
        val componentName = AdminReceiver.getComponentName(requireContext())
        val isAdmin = dpm?.isAdminActive(componentName) == true
        
        statusText.text = if (isAdmin) getString(R.string.enabled) else getString(R.string.disabled)
    }

    fun onAdminResult() {
        // Refresh status
    }
}

class AdminReceiver : android.app.admin.DeviceAdminReceiver() {
    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, AdminReceiver::class.java)
        }
    }
}
