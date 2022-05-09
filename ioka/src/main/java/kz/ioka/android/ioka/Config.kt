package kz.ioka.android.ioka

internal object Config {

    lateinit var apiKey: String
    var isDebug: Boolean = true

    fun isApiKeyInitialized() = this::apiKey.isInitialized

}