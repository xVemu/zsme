package pl.vemu.zsme.util

import android.text.format.DateUtils
import java.util.Date

object Formatter {
    fun relativeDate(date: Date) = DateUtils.getRelativeTimeSpanString(
        date.time,
        Date().time,
        DateUtils.DAY_IN_MILLIS
    ).toString()
}
