package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentGenericBinding

/**
 * Step 9: OEM Whitelist Instructions
 */
class OemWhitelistFragment : Fragment() {

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
            textViewTitle.text = "OEM Specific Settings"
            textViewDescription.text = """
                Some device manufacturers require additional settings:
                
                Xiaomi/Redmi/Poco:
                • Enable "Autostart" in Security app
                • Lock the app in recent apps
                
                Samsung:
                • Add to "Never sleeping apps"
                • Enable in "Put unused apps to sleep" exceptions
                
                OnePlus/Oppo/Realme:
                • Enable in "Battery optimization" exceptions
                • Lock in recent apps
                
                Huawei/Honor:
                • Enable "Launch manager" auto-launch
                • Disable power-intensive cleanup
                
                Vivo:
                • Enable "Background power consumption" high usage
                • Lock in recent apps
                
                Tap "I Understand" to complete setup.
            """.trimIndent()
            buttonAction.text = "I Understand"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
