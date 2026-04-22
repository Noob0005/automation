package com.myremote.admin.wizard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.myremote.admin.R
import com.myremote.admin.data.SecureStorage
import com.myremote.admin.databinding.ActivityWizardBinding

class WizardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWizardBinding
    private lateinit var secureStorage: SecureStorage
    private lateinit var wizardAdapter: WizardAdapter

    companion object {
        const val TOTAL_STEPS = 9
        
        fun newIntent(context: Context): Intent {
            return Intent(context, WizardActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWizardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        secureStorage = SecureStorage(this)
        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        wizardAdapter = WizardAdapter(this)
        binding.viewPager.adapter = wizardAdapter
        binding.viewPager.isUserInputEnabled = false // Disable swipe, use buttons only

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()
    }

    private fun setupButtons() {
        binding.buttonNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < TOTAL_STEPS - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                completeWizard()
            }
        }

        binding.buttonBack.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem > 0) {
                binding.viewPager.currentItem = currentItem - 1
            }
        }

        updateButtonStates(0)
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonStates(position)
                updateStepIndicator(position)
            }
        })
    }

    private fun updateButtonStates(position: Int) {
        binding.buttonBack.isEnabled = position > 0
        binding.buttonBack.alpha = if (position > 0) 1.0f else 0.5f
        
        if (position == TOTAL_STEPS - 1) {
            binding.buttonNext.text = "Complete Setup"
        } else {
            binding.buttonNext.text = "Next"
        }
    }

    private fun updateStepIndicator(position: Int) {
        binding.textViewStep.text = "Step ${position + 1} of $TOTAL_STEPS"
        binding.progressBar.progress = ((position + 1).toFloat() / TOTAL_STEPS * 100).toInt()
    }

    private fun completeWizard() {
        // Generate device key if not exists
        if (secureStorage.getDeviceKey() == null) {
            secureStorage.generateDeviceKey()
        }
        
        // Mark setup as complete
        secureStorage.setSetupComplete(true)
        
        // Return to main activity
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
