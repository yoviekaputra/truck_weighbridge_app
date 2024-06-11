package android.template.core.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

val currentDateTime: Date
    get() = Calendar.getInstance().time

val Date.currentHours: Int
    get() = runCatching { toString("HH").toIntOrNull() }.getOrNull() ?: 0

val Date.currentMinutes: Int
    get() = runCatching { toString("mm").toIntOrNull() }.getOrNull() ?: 0

val Long.asTimeOfHours: Int
    get() {
        val current = currentDateTime
        current.time = this
        return current.currentHours
    }

val Long.asTimeOfMinutes: Int
    get() {
        val current = currentDateTime
        current.time = this
        return current.currentMinutes
    }

fun Long.asDateFormatted(format: String, locale: Locale = Locale.getDefault()): String {
    val current = currentDateTime
    current.time = this
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(current)
}