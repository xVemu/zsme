package pl.vemu.zsme

sealed class State<T> {
    data class Success<T>(val data: T) : State<T>()
    data class Error<T>(val throwable: Throwable) : State<T>()
    class Loading<T> : State<T>()
}