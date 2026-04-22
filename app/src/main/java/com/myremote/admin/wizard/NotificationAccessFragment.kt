package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentGenericBinding

/**
 * Step 4: Notification Access Permission
 */
class NotificationAccessFragment : Fragment() {

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
            textViewTitle.text = "Notification Access"
            textViewDescription.text = """
                This app needs Notification Access to:
                
                • Read incoming notifications remotely
                • Monitor notification status
                • Clear notifications on command
                
                Tap "Grant Access" to enable Notification Access in your device settings.
            """.trimIndent()
            buttonAction.text = "Grant Access"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
