package kz.ioka.android.ioka.util

import kz.ioka.android.ioka.presentation.flows.common.CardType

fun String?.toCardType(): CardType {
    if (this == null) {
        return CardType.UNKNOWN
    }

    return CardType.values().first { it.code == this }
}