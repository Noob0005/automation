package com.myremote.admin.wizard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter for the 9-screen wizard
 */
class WizardAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val NUM_SCREENS = 9
    }

    override fun getItemCount(): Int = NUM_SCREENS

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WelcomeFragment()
            1 -> PermissionsFragment()
            2 -> AccessibilityPermissionFragment()
            3 -> NotificationAccessFragment()
            4 -> FileAccessFragment()
            5 -> BatteryOptimizationFragment()
            6 -> DeviceAdminFragment()
            7 -> DeviceKeyFragment()
            8 -> OemWhitelistFragment()
            else -> WelcomeFragment()
        }
    }
}
