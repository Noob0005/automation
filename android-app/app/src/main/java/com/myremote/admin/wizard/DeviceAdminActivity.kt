package com.myremote.admin.wizard

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.myremote.admin.R

class DeviceAdminActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(this).inflate(R.layout.activity_device_admin, null))

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, AdminReceiver::class.java)

        findViewById<TextView>(R.id.titleText).text = getString(R.string.device_admin_title)
        findViewById<TextView>(R.id.descText).text = getString(R.string.device_admin_desc)

        findViewById<Button>(R.id.activateButton).setOnClickListener {
            activateDeviceAdmin()
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (isDeviceAdminActive()) {
                startActivity(Intent(this, KeyGenerationActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please activate device admin first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun activateDeviceAdmin() {
        if (!isDeviceAdminActive()) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
                "MyRemote Admin needs device administrator privileges for remote management")
            try {
                startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN)
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open device admin settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DEVICE_ADMIN) {
            if (isDeviceAdminActive()) {
                Toast.makeText(this, "Device admin activated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_DEVICE_ADMIN = 1001
    }
}
