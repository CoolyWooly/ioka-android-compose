package kz.ioka.android.ioka

internal object Config {

    lateinit var apiKey: String

    fun isApiKeyInitialized() = this::apiKey.isInitialized

}