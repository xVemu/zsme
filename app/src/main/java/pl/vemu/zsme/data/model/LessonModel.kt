package pl.vemu.zsme.data.model

data class LessonModel(
    val name: String,
    val room: String?,
    val teacher: String?,
    val timeStart: String,
    val timeFinish: String,
    val index: Int?,
)
