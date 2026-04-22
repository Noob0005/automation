package com.myremote.admin.wizard

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentGenericBinding

/**
 * Step 7: Device Admin Permission
 */
class DeviceAdminFragment : Fragment() {

    private var _binding: FragmentGenericBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.apply {
            textViewTitle.text = "Device Administrator"
            textViewDescription.text = """
                This app needs Device Administrator permission to:
                
                • Lock the device remotely
                • Ring the device at full volume
                • Enhanced security features
                • Prevent easy uninstallation
                
                Tap "Activate" to grant Device Administrator rights.
            """.trimIndent()
            buttonAction.text = "Activate"
            
            buttonAction.setOnClickListener {
                requestDeviceAdmin()
            }
        }
        
        checkDeviceAdmin()
    }

    private fun checkDeviceAdmin() {
        val devicePolicyManager = ContextCompat.getSystemService(
            requireContext(), 
            DevicePolicyManager::class.java
        )
        val componentName = ComponentName(requireContext(), AdminReceiver::class.java)
        val isAdmin = devicePolicyManager?.isAdminActive(componentName) == true
        
        if (isAdmin) {
            binding.buttonAction.isEnabled = false
            binding.buttonAction.alpha = 0.5f
        }
    }

    private fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(requireContext(), AdminReceiver::class.java))
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Required for remote lock and ring features")
        }
        startActivityForResult(intent, REQUEST_DEVICE_ADMIN)
    }

    companion object {
        private const val REQUEST_DEVICE_ADMIN = 1001
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Simple Device Admin Receiver
class AdminReceiver : android.app.admin.DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }
    
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }
}
