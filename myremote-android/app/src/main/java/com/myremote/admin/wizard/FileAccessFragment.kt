package com.myremote.admin.wizard

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.myremote.admin.R

class FileAccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_files, container, false)
        
        val grantButton = view.findViewById<MaterialButton>(R.id.grantFilesButton)
        val statusText = view.findViewById<MaterialTextView>(R.id.fileAccessStatusText)
        
        checkFileAccess(statusText)
        
        grantButton.setOnClickListener {
            (activity as? WizardActivity)?.requestFileAccess()
        }
        
        return view
    }

    private fun checkFileAccess(statusText: MaterialTextView) {
        val hasAccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Settings.ManagedProfile.provisionActiveProfile(null, requireContext())
            true // Simplified - in real app check Environment.isExternalStorageManager()
        } else {
            true // Lower versions have simpler permissions
        }
        
        statusText.text = if (hasAccess) getString(R.string.granted) else getString(R.string.denied)
    }

    fun onFileAccessResult() {
        // Refresh status
    }
}
