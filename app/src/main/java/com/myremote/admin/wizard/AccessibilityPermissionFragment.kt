package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentGenericBinding

/**
 * Step 3: Accessibility Permission
 */
class AccessibilityPermissionFragment : Fragment() {

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
            textViewTitle.text = "Accessibility Service"
            textViewDescription.text = """
                This app needs Accessibility Service permission to:
                
                • Perform tap gestures at specific coordinates
                • Execute swipe gestures
                • Type text remotely
                • Navigate the device interface
                
                Tap "Grant Access" to enable the Accessibility Service in your device settings.
                
                Note: This permission only allows remote control commands you explicitly send.
            """.trimIndent()
            buttonAction.text = "Grant Access"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
