package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.myremote.admin.data.SecureStorage
import com.myremote.admin.databinding.FragmentDeviceKeyBinding

/**
 * Step 8: Device Key Generation and Display
 */
class DeviceKeyFragment : Fragment() {

    private var _binding: FragmentDeviceKeyBinding? = null
    private val binding get() = _binding!!
    private lateinit var secureStorage: SecureStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceKeyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        secureStorage = SecureStorage(requireContext())
        
        binding.apply {
            textViewTitle.text = "Your Device Key"
            textViewDescription.text = """
                This unique 32-character key identifies your device.
                
                You will need to enter this key in the web console to connect to this device.
                
                Keep this key secure and do not share it with anyone you don't trust.
            """.trimIndent()
            
            // Generate or retrieve device key
            var deviceKey = secureStorage.getDeviceKey()
            if (deviceKey == null) {
                deviceKey = secureStorage.generateDeviceKey()
            }
            
            textViewDeviceKey.text = formatDeviceKey(deviceKey)
            buttonCopy.setOnClickListener {
                // Copy to clipboard functionality
                copyToClipboard(deviceKey)
            }
            buttonRegenerate.setOnClickListener {
                val newKey = secureStorage.generateDeviceKey()
                textViewDeviceKey.text = formatDeviceKey(newKey)
            }
        }
    }

    private fun formatDeviceKey(key: String): String {
        // Format as XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
        return key.chunked(4).joinToString("-")
    }

    private fun copyToClipboard(key: String) {
        // TODO: Implement clipboard copy
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
