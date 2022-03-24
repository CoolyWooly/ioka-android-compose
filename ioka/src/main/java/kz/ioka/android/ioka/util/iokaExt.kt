package kz.ioka.android.ioka.util

internal fun String.getCustomerId(): String {
    val customerIdEndIndex = indexOf("_secret")

    return substring(0, customerIdEndIndex)
}

internal fun String.getOrderId(): String {
    val customerIdEndIndex = indexOf("_secret")

    return substring(0, customerIdEndIndex)
}