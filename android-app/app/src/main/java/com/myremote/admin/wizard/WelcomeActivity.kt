package com.myremote.admin.wizard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myremote.admin.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_welcome, null))

        findViewById<TextView>(R.id.titleText).text = getString(R.string.welcome_title)
        findViewById<TextView>(R.id.subtitleText).text = getString(R.string.welcome_subtitle)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            startActivity(Intent(this, PermissionGuideActivity::class.java))
            finish()
        }
    }
}
