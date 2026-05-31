package com.example.assetstar.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L
    private val displayFormat = ThreadLocal.withInitial {
        SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
    }

    fun formatDate(millis: Long?): String {
        if (millis == null || millis <= 0L) return "--"
        return checkNotNull(displayFormat.get()).format(Date(millis))
    }

    fun todayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        return toStartOfDayMillis(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            day = calendar.get(Calendar.DAY_OF_MONTH),
        )
    }

    fun toStartOfDayMillis(
        year: Int,
        month: Int,
        day: Int,
    ): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun daysBetween(startMillis: Long, endMillis: Long): Int {
        return maxOf(1, ((normalize(endMillis) - normalize(startMillis)) / DAY_IN_MILLIS).toInt())
    }

    private fun normalize(value: Long): Long = value - (value % DAY_IN_MILLIS)
}
