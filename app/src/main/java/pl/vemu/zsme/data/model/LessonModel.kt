package pl.vemu.zsme.data.model

data class LessonModel(
    val name: String,
    val room: String?,
    val teacher: String?,
    var timeStart: String? = null,
    var timeFinish: String? = null,
    var index: Int? = null,
)