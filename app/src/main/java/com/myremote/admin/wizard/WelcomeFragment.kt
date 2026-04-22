package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentWelcomeBinding

/**
 * Step 1: Welcome screen
 */
class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.apply {
            textViewTitle.text = "Welcome to MyRemote Admin"
            textViewDescription.text = """
                This app allows you to remotely administer your Android device from a web browser.
                
                You will need to grant several permissions during setup to enable full functionality.
                
                Let's get started!
            """.trimIndent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
