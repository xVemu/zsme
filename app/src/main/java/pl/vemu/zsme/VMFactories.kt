package pl.vemu.zsme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.vemu.zsme.data.repo.DetailRepo
import pl.vemu.zsme.data.repo.LessonRepo
import pl.vemu.zsme.ui.detail.DetailFragmentVM
import pl.vemu.zsme.ui.timetable.LessonFragmentVM

class DetailFragmentVMFactory constructor(
        private val detailRepo: DetailRepo,
        private val content: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailFragmentVM::class.java)) return DetailFragmentVM(detailRepo, content) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LessonFragmentVMFactory constructor(
        private val lessonRepo: LessonRepo,
        private val url: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonFragmentVM::class.java)) return LessonFragmentVM(lessonRepo, url) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}