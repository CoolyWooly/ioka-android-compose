package kz.ioka.android.ioka.presentation.flows.common

import androidx.annotation.DrawableRes
import kz.ioka.android.ioka.R

internal enum class CardType(@DrawableRes val cardTypeRes: Int, val code: String) {

    AMEX(R.drawable.ioka_ic_ps_amex, "AMERICAN_EXPRESS"),
    DINERS_CLUB(R.drawable.ioka_ic_ps_diners_club, "DINER_CLUB"),
    MAESTRO(R.drawable.ioka_ic_ps_maestro, "MAESTRO"),
    MASTERCARD(R.drawable.ioka_ic_ps_mastercard, "MASTERCARD"),
    MIR(R.drawable.ioka_ic_ps_mir, "MIR"),
    UNION_PAY(R.drawable.ioka_ic_ps_unionpay, "UNION_PAY"),
    VISA(R.drawable.ioka_ic_ps_visa, "VISA"),
    UNKNOWN(R.drawable.ioka_ic_ps_visa, "UNKNOWN")

}