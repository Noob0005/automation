package com.myremote.admin.wizard

import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.myremote.admin.R

class BatteryOptimizationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_battery, container, false)
        
        val disableButton = view.findViewById<MaterialButton>(R.id.disableBatteryButton)
        val statusText = view.findViewById<MaterialTextView>(R.id.batteryStatusText)
        
        checkBatteryStatus(statusText)
        
        disableButton.setOnClickListener {
            (activity as? WizardActivity)?.requestBatteryOptimizationExemption()
        }
        
        return view
    }

    private fun checkBatteryStatus(statusText: MaterialTextView) {
        val pm = requireContext().getSystemService(PowerManager::class.java)
        val isIgnoring = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.isIgnoringBatteryOptimizations(requireContext().packageName)
        } else {
            true
        }
        
        statusText.text = if (isIgnoring) getString(R.string.enabled) else getString(R.string.disabled)
    }

    fun onBatteryResult() {
        // Refresh status
    }
}
