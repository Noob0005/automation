package com.myremote.admin.wizard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentPermissionsBinding

/**
 * Step 2: Basic Permissions Request
 */
class PermissionsFragment : Fragment() {

    private var _binding: FragmentPermissionsBinding? = null
    private val binding get() = _binding!!

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        updateUI(allGranted)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.apply {
            textViewTitle.text = "Grant Permissions"
            textViewDescription.text = """
                This app needs several permissions to function:
                
                • Camera - Take photos remotely
                • Microphone - Record audio
                • Location - Track device location
                • Storage - Access files and media
                
                Tap the button below to grant these permissions.
            """.trimIndent()
            
            buttonGrantPermissions.setOnClickListener {
                requestPermissions()
            }
        }
        
        checkPermissions()
    }

    private fun checkPermissions() {
        val grantedCount = requiredPermissions.count {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        updateUI(grantedCount == requiredPermissions.size)
    }

    private fun requestPermissions() {
        permissionLauncher.launch(requiredPermissions)
    }

    private fun updateUI(allGranted: Boolean) {
        binding.apply {
            if (allGranted) {
                textStatus.text = "✓ All permissions granted"
                textStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                buttonGrantPermissions.isEnabled = false
                buttonGrantPermissions.alpha = 0.5f
            } else {
                textStatus.text = "⚠ Some permissions still needed"
                textStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
                buttonGrantPermissions.isEnabled = true
                buttonGrantPermissions.alpha = 1.0f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
