package com.myremote.admin.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.myremote.admin.model.CommandType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GestureAccessibilityService : AccessibilityService() {

    companion object {
        const val ACTION_EXECUTE_COMMAND = "com.myremote.admin.ACTION_EXECUTE_COMMAND"
        const val EXTRA_COMMAND_TYPE = "command_type"
        const val EXTRA_COMMAND_DATA = "command_data"
        
        private var instance: GestureAccessibilityService? = null
        val isEnabled: StateFlow<Boolean> = MutableStateFlow(false)
        
        fun getInstance(): GestureAccessibilityService? = instance
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        (isEnabled as MutableStateFlow).value = true
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        (isEnabled as MutableStateFlow).value = false
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Event handling for monitoring
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    fun performTap(x: Int, y: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
            val gesture = GestureDescription.StrokeDescription(path, 0, 100)
            val builder = GestureDescription.Builder().addStroke(gesture)
            return dispatchGesture(builder.build(), null, null)
        }
        return false
    }

    fun performSwipe(startX: Int, startY: Int, endX: Int, endY: Int, duration: Long = 300): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply {
                moveTo(startX.toFloat(), startY.toFloat())
                lineTo(endX.toFloat(), endY.toFloat())
            }
            val gesture = GestureDescription.StrokeDescription(path, 0, duration)
            val builder = GestureDescription.Builder().addStroke(gesture)
            return dispatchGesture(builder.build(), null, null)
        }
        return false
    }

    fun performBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun performHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun performRecentApps(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    fun typeText(text: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return performGlobalAction(GLOBAL_ACTION_ACCESSIBILITY_SHORTCUT)
        }
        // Fallback: use input method
        return false
    }

    fun findAndClickByText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        
        for (node in nodes) {
            if (node.isClickable) {
                val rect = Rect()
                node.getBoundsInScreen(rect)
                return performTap(rect.centerX(), rect.centerY())
            }
        }
        return false
    }

    fun findAndClickByContentDesc(desc: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(desc)
        
        for (node in nodes) {
            if (node.isClickable) {
                val rect = Rect()
                node.getBoundsInScreen(rect)
                return performTap(rect.centerX(), rect.centerY())
            }
        }
        return false
    }

    fun getScreenshot(): ByteArray? {
        return try {
            val rootNode = rootInActiveWindow ?: return null
            // Screenshot capability through accessibility
            null
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        (isEnabled as MutableStateFlow).value = false
    }
}
