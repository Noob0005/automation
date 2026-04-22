package com.myremote.admin.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.myremote.admin.R

class NotificationAccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard_notification, container, false)
        
        val enableButton = view.findViewById<MaterialButton>(R.id.enableNotificationButton)
        
        enableButton.setOnClickListener {
            (activity as? WizardActivity)?.openNotificationAccessSettings()
        }
        
        return view
    }
}
