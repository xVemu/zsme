package pl.vemu.zsme.data

import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.db.TimetableDAO
import pl.vemu.zsme.data.repo.TimetableRepo
import javax.inject.Inject

@Single
class TimetableMediator(
    private val dao: TimetableDAO,
    private val repo: TimetableRepo,
) {
    fun getTimetable() = flow {
        emit(Result.Loading)

        try {
            val items = dao.getAll()
            if (items.isNotEmpty())
                emit(Result.Success(items, refreshing = true))

            try {
                val newItems = repo.getTimetable()
                emit(Result.Success(newItems))
                dao.updateAll(newItems)
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
