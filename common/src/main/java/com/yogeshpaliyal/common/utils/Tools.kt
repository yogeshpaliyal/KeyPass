package com.yogeshpaliyal.common.utils

import java.text.NumberFormat
import java.util.Locale

object Tools {
    @JvmStatic
    fun formatTokenString(token: Int, digits: Int): String {
        val numberFormat = NumberFormat.getInstance(Locale.ENGLISH)
        numberFormat.minimumIntegerDigits = digits
        numberFormat.isGroupingUsed = false
        return numberFormat.format(token.toLong())
    }
}
