package pl.vemu.zsme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.vemu.zsme.detailedNews.DetailFragmentVM
import pl.vemu.zsme.timetableFragment.lesson.LessonFragmentVM

class VMFactory(private val url: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonFragmentVM::class.java)) return LessonFragmentVM(url) as T
        else if (modelClass.isAssignableFrom(DetailFragmentVM::class.java)) return DetailFragmentVM(url) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}