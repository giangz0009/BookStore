package com.pro.book.utils

import android.util.Patterns

object StringUtil {
    @JvmStatic
    fun isEmpty(input: String?): Boolean {
        return input.isNullOrEmpty() || input.trim { it <= ' ' }.isEmpty()
    }

    @JvmStatic
    fun isValidEmail(target: CharSequence?): Boolean {
        if (target == null) return false
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun getDoubleNumber(number: Int): String {
        return if (number < 10) {
            "0$number"
        } else "" + number
    }
}
