package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.myremote.admin.R
import com.myremote.admin.data.SecureStorage

class KeyGenerationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_key, container, false)
        
        val generateButton = view.findViewById<MaterialButton>(R.id.generateKeyButton)
        val keyText = view.findViewById<MaterialTextView>(R.id.generatedKeyText)
        
        // Check if key already exists
        val existingKey = SecureStorage.getDeviceKey(requireContext())
        if (!existingKey.isNullOrEmpty()) {
            keyText.text = formatKey(existingKey)
            generateButton.isEnabled = false
            generateButton.text = "Key Generated"
        }
        
        generateButton.setOnClickListener {
            (activity as? WizardActivity)?.generateAndSaveKey()
        }
        
        return view
    }

    fun onKeyGenerated(key: String) {
        val keyText = view?.findViewById<MaterialTextView>(R.id.generatedKeyText)
        keyText?.text = formatKey(key)
        
        val generateButton = view?.findViewById<MaterialButton>(R.id.generateKeyButton)
        generateButton?.isEnabled = false
        generateButton?.text = "Key Generated"
        
        Toast.makeText(requireContext(), "Device key generated!", Toast.LENGTH_SHORT).show()
    }

    private fun formatKey(key: String): String {
        return key.chunked(4).joinToString("-")
    }
}
