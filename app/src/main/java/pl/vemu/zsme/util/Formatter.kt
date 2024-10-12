package pl.vemu.zsme.util

import android.text.format.DateUtils
import java.text.DateFormat
import java.util.Date

object Formatter {

    /**
     * Example: 'today', 'yesterday'
     * */
    fun relativeDate(date: Date) = DateUtils.getRelativeTimeSpanString(
        date.time,
        Date().time,
        DateUtils.DAY_IN_MILLIS
    ).toString()

    /**
     * Example: '16 Nov 2023, 09:23:11'
     * */
    fun fullDate(date: Date): String = DateFormat.getDateTimeInstance().format(date) // TODO DateTimeFormatter
}
