package com.myremote.admin.data

object DeviceKeyGenerator {

    private const val KEY_LENGTH = 32
    private const val CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Excluding similar chars

    fun generate(): String {
        val random = java.security.SecureRandom()
        return buildString(KEY_LENGTH) {
            repeat(KEY_LENGTH) {
                append(CHARS[random.nextInt(CHARS.length)])
            }
        }
    }

    fun isValid(key: String): Boolean {
        if (key.length != KEY_LENGTH) return false
        return key.all { it in CHARS }
    }
}
