package com.myremote.admin.wizard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentGenericBinding

/**
 * Step 6: Battery Optimization Exemption
 */
class BatteryOptimizationFragment : Fragment() {

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
            textViewTitle.text = "Battery Optimization"
            textViewDescription.text = """
                To ensure the remote admin service runs continuously:
                
                • Disable battery optimization for this app
                • Allow background activity
                • Prevent the system from killing the service
                
                Tap "Disable Optimization" to exempt this app from battery optimizations.
            """.trimIndent()
            buttonAction.text = "Disable Optimization"
            
            buttonAction.setOnClickListener {
                requestBatteryOptimizationExemption()
            }
        }
        
        checkBatteryOptimization()
    }

    private fun checkBatteryOptimization() {
        val powerManager = ContextCompat.getSystemService(requireContext(), PowerManager::class.java)
        val isExempted = powerManager?.isIgnoringBatteryOptimizations(requireContext().packageName) == true
        
        if (isExempted) {
            binding.buttonAction.isEnabled = false
            binding.buttonAction.alpha = 0.5f
        }
    }

    private fun requestBatteryOptimizationExemption() {
        val intent = Intent().apply {
            action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = Uri.parse("package:${requireContext().packageName}")
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
