package kz.ioka.android.iokademoapp.common

object LocaleUtils {

    fun getLocaleByValue(value: String): Locale {
        return when (value) {
            Locale.EN.value -> Locale.EN
            Locale.KZ.value -> Locale.KZ
            else -> Locale.RU
        }
    }

}

enum class Locale(val value: String) {
    RU("ru"),
    EN("en"),
    KZ("kk");
}