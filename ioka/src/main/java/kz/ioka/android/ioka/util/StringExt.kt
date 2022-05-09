package kz.ioka.android.ioka.util

internal fun String.shortPanMask(): String {
    return "•••• ${takeLast(4)}"
}