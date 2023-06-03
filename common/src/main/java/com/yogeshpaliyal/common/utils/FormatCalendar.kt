package com.yogeshpaliyal.common.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 23-03-2021 22:30
*/

fun Long.formatCalendar(dateTimeFormat: String?): String? {
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val simpleDateFormat = SimpleDateFormat(dateTimeFormat, Locale.US)
    return simpleDateFormat.format(calendar.time)
}
