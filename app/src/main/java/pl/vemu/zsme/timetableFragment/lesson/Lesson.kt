package pl.vemu.zsme.timetableFragment.lesson

data class Lesson(
        val name: String,
        val room: String,
        val teacher: String,
        var timeStart: String? = null,
        var timeFinish: String? = null,
        var index: Int? = null,
)