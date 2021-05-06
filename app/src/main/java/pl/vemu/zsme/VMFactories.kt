package pl.vemu.zsme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.vemu.zsme.repo.DetailRepo
import pl.vemu.zsme.timetableFragment.lesson.LessonFragmentVM
import pl.vemu.zsme.ui.DetailFragmentVM

class LessonFragmentVMFactory(private val url: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonFragmentVM::class.java)) return LessonFragmentVM(url) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DetailFragmentVMFactory constructor(
        private val detailRepo: DetailRepo,
        private val content: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailFragmentVM::class.java)) return DetailFragmentVM(detailRepo, content) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}