package com.myremote.admin.wizard

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.myremote.admin.R

class PermissionsFragment : Fragment() {

    private var allGranted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_permissions, container, false)
        
        val grantButton = view.findViewById<MaterialButton>(R.id.grantPermissionsButton)
        val statusText = view.findViewById<MaterialTextView>(R.id.permissionStatusText)
        
        // Check initial permission status
        checkPermissions(statusText)
        
        grantButton.setOnClickListener {
            (activity as? WizardActivity)?.requestPermissions()
        }
        
        return view
    }

    private fun checkPermissions(statusText: MaterialTextView) {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        
        val grantedCount = permissions.count {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        
        allGranted = grantedCount == permissions.size
        statusText.text = if (allGranted) 
            getString(R.string.granted) 
            else "${grantedCount}/${permissions.size} ${getString(R.string.granted)}"
    }

    fun onPermissionsResult(allGranted: Boolean) {
        this.allGranted = allGranted
        // Update UI if needed
    }
}
