package pl.vemu.zsme.data

import kotlinx.coroutines.flow.flow
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.db.LessonDAO
import pl.vemu.zsme.data.repo.LessonRepo
import javax.inject.Inject

class LessonMediator @Inject constructor(
    private val dao: LessonDAO,
    private val repo: LessonRepo,
) {
    fun getLessons(url: String) = flow {
        emit(Result.Loading)

        try {
            val items = dao.getAllWithUrl(url)
            if (items.isNotEmpty())
                emit(Result.Success(items, refreshing = true))

            try {
                val newItems = repo.getLesson(url)
                emit(Result.Success(newItems))
                dao.updateAllWithUrl(url, newItems)
            } catch (e: Exception) {
                if (items.isEmpty())
                    emit(Result.Failure(e))
                else
                    emit(Result.Success(items, error = e))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}
