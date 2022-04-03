package kz.ioka.android.ioka.domain.errorHandler

internal sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class IokaError(val code: String, val message: String) : ResultWrapper<Nothing>()

    data class HttpError(val code: Int? = null, val error: ErrorResponse? = null) :
        ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()
}