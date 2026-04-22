package com.myremote.admin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myremote.admin.data.SecureStorage
import com.myremote.admin.databinding.ActivityMainBinding
import com.myremote.admin.wizard.WizardActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var secureStorage: SecureStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        secureStorage = SecureStorage(this)

        // Check if setup is complete
        if (!secureStorage.isSetupComplete()) {
            launchWizard()
            return
        }

        // Verify device key exists
        if (secureStorage.getDeviceKey() == null) {
            launchWizard()
            return
        }

        // Show main dashboard or status
        showDashboard()
    }

    private fun launchWizard() {
        val intent = WizardActivity.newIntent(this)
        startActivity(intent)
        finish()
    }

    private fun showDashboard() {
        // TODO: Implement dashboard UI
        binding.apply {
            textViewStatus.text = "Remote Admin Active"
            textViewDeviceKey.text = "Device Key: ${secureStorage.getDeviceKey()?.take(8)}..."
            buttonStartService.setOnClickListener {
                // Start or restart service
            }
        }
    }
}
