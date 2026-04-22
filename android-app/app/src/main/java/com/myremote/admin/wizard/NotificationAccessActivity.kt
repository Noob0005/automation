package com.myremote.admin.wizard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myremote.admin.R

class NotificationAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_notification_access, null))

        findViewById<TextView>(R.id.titleText).text = getString(R.string.notification_access_title)
        findViewById<TextView>(R.id.descText).text = getString(R.string.notification_access_desc)

        findViewById<Button>(R.id.openSettingsButton).setOnClickListener {
            openNotificationAccessSettings()
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            startActivity(Intent(this, FileAccessActivity::class.java))
            finish()
        }
    }

    private fun openNotificationAccessSettings() {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
}
