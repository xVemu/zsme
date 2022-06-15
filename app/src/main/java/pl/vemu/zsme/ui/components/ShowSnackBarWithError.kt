package pl.vemu.zsme.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import java.net.UnknownHostException

@Composable
fun ShowSnackBarWithError(
    result: Result<*>,
    snackbarHostState: SnackbarHostState,
    onActionPerformed: () -> Unit
) {
    val errorMsg = stringResource(R.string.error)
    val retryMsg = stringResource(R.string.retry)
    val noConnectionMsg = stringResource(R.string.no_connection)
    val error = (result as Result.Failure).error
    LaunchedEffect(error) {
        val snackbarResult = snackbarHostState.showSnackbar(
            message = if (error is UnknownHostException) noConnectionMsg else errorMsg,
            actionLabel = retryMsg,
            duration = SnackbarDuration.Indefinite
        )
        if (snackbarResult == SnackbarResult.ActionPerformed)
            onActionPerformed()
    }
}