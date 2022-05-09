package kz.ioka.android.ioka.domain.errorHandler

import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


internal suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    convertErrorBody(throwable)?.let {
                        ResultWrapper.IokaError(it.code, it.message)
                    } ?: ResultWrapper.HttpError(null, null)
                }
                else -> {
                    ResultWrapper.HttpError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.string().let {
            val gsonAdapter = GsonBuilder().create().getAdapter(ErrorResponse::class.java)
            gsonAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        null
    }
}