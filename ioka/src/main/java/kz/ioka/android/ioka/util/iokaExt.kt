package kz.ioka.android.ioka.util

fun String.getCustomerId(): String {
    val customerIdEndIndex = indexOf("_secret")

    return substring(0, customerIdEndIndex)
}

fun String.getOrderId(): String {
    val customerIdEndIndex = indexOf("_secret")

    return substring(0, customerIdEndIndex)
}