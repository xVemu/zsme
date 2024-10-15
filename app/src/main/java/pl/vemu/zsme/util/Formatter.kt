package pl.vemu.zsme.util

import android.text.format.DateUtils
import kotlinx.datetime.*
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Formatter {
    /**
     * Example: 'today', 'yesterday'
     * */
    fun relativeDate(date: LocalDateTime) = DateUtils.getRelativeTimeSpanString(
        date.toInstant(UtcOffset.ZERO).toEpochMilliseconds(),
        Clock.System.now().toEpochMilliseconds(),
        DateUtils.DAY_IN_MILLIS
    ).toString()

    /**
     * Example: '16 Nov 2023, 09:23:11'
     * */
    fun fullDate(date: LocalDateTime): String =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
            .format(date.toJavaLocalDateTime().atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()))

    fun hourMinute(time: LocalTime): String =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(time.toJavaLocalTime()) // ignore timezones
}

object Parser {
    private val hourMinute = LocalTime.Format {
        hour(padding = Padding.NONE)
        char(':')
        minute()
    }

    fun parseHourMinute(time: String) = hourMinute.parse(time)
}
