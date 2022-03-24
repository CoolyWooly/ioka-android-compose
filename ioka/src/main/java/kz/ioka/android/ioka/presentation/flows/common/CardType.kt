package kz.ioka.android.ioka.presentation.flows.common

import androidx.annotation.DrawableRes
import kz.ioka.android.ioka.R

internal enum class CardType(@DrawableRes val cardTypeRes: Int, val code: String) {

    AMEX(R.drawable.ic_ps_amex, "AMERICAN_EXPRESS"),
    DINERS_CLUB(R.drawable.ic_ps_diners_club, "DINER_CLUB"),
    MAESTRO(R.drawable.ic_ps_maestro, "MAESTRO"),
    MASTERCARD(R.drawable.ic_ps_mastercard, "MASTERCARD"),
    MIR(R.drawable.ic_ps_mir, "MIR"),
    UNION_PAY(R.drawable.ic_ps_unionpay, "UNION_PAY"),
    VISA(R.drawable.ic_ps_visa, "VISA"),
    UNKNOWN(R.drawable.ic_ps_visa, "UNKNOWN")

}