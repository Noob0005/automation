package com.myremote.admin.wizard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.myremote.admin.R
import com.myremote.admin.service.RemoteAdminService
import com.myremote.admin.ui.MainActivity
import com.myremote.admin.util.DeviceKeyManager

class KeyGenerationActivity : AppCompatActivity() {

    private var generatedKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_key_generation, null))

        findViewById<TextView>(R.id.titleText).text = getString(R.string.key_gen_title)
        findViewById<TextView>(R.id.descText).text = getString(R.string.key_gen_desc)

        // Check if key already exists
        val existingKey = DeviceKeyManager.getDeviceKey()
        if (existingKey != null) {
            generatedKey = existingKey
            showKey(existingKey)
        }

        findViewById<Button>(R.id.generateButton).setOnClickListener {
            generateAndSaveKey()
        }

        findViewById<Button>(R.id.copyButton).setOnClickListener {
            copyKeyToClipboard()
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (generatedKey != null) {
                startActivity(Intent(this, OemWhitelistActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please generate a key first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateAndSaveKey() {
        generatedKey = DeviceKeyManager.generateDeviceKey()
        DeviceKeyManager.saveDeviceKey(generatedKey!!)
        showKey(generatedKey!!)
        Toast.makeText(this, "Key generated and saved securely!", Toast.LENGTH_LONG).show()
    }

    private fun showKey(key: String) {
        val keyTextView = findViewById<TextView>(R.id.keyTextView)
        // Format key in groups of 8 for readability
        val formattedKey = key.chunked(8).joinToString("-")
        keyTextView.text = formattedKey
        keyTextView.visibility = TextView.VISIBLE
        
        findViewById<Button>(R.id.copyButton).isEnabled = true
        findViewById<Button>(R.id.nextButton).isEnabled = true
    }

    private fun copyKeyToClipboard() {
        generatedKey?.let { key ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Device Key", key)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Key copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }
}
