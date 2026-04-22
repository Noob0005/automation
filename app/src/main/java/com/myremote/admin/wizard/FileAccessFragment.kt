package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myremote.admin.databinding.FragmentGenericBinding

/**
 * Step 5: File Access Permission (SAF)
 */
class FileAccessFragment : Fragment() {

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
            textViewTitle.text = "File Access"
            textViewDescription.text = """
                This app needs file access to:
                
                • Browse device files remotely
                • Upload and download files
                • Access photos and media
                • Manage documents
                
                Tap "Grant Access" to enable file access using Storage Access Framework.
            """.trimIndent()
            buttonAction.text = "Grant Access"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
