package pl.vemu.zsme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.vemu.zsme.data.repo.LessonRepo
import pl.vemu.zsme.ui.timetable.LessonVM

class LessonFragmentVMFactory constructor(
    private val lessonRepo: LessonRepo,
    private val url: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonVM::class.java)) return LessonVM(lessonRepo, url) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}