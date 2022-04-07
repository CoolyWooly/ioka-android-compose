package kz.ioka.android.ioka.domain.cardInfo

import androidx.annotation.DrawableRes
import kz.ioka.android.ioka.R

internal enum class CardBrandModel(
    val code: String,
    @DrawableRes val iconRes: Int?
) {

    Amex("American Express", R.drawable.ioka_ic_ps_amex),
    DinersClub("DinnersClub", R.drawable.ioka_ic_ps_diners_club),
    Maestro("MAESTRO", R.drawable.ioka_ic_ps_maestro),
    MasterCard("MASTERCARD", R.drawable.ioka_ic_ps_mastercard),
    Mir("МИР", R.drawable.ioka_ic_ps_mir),
    UnionPay("UnionPAY", R.drawable.ioka_ic_ps_unionpay),
    Visa("VISA", R.drawable.ioka_ic_ps_visa),
    Unknown("UNKNOWN", null)

}

internal enum class CardEmitterModel(
    val code: String,
    val iconRes: Int?,
) {

    Alfa("alfabank", R.drawable.ioka_ic_bank_alfa),
    Altyn("altynbank", R.drawable.ioka_ic_bank_altyn),
    Atf("atfbank", R.drawable.ioka_ic_bank_atf),
    Bcc("centercredit", R.drawable.ioka_ic_bank_bcc),
    Eurasian("eurasianbank", R.drawable.ioka_ic_bank_eurasian),
    Forte("fortebank", R.drawable.ioka_ic_bank_forte),
    FreedomFinance("freedom", R.drawable.ioka_ic_bank_freedom),
    Halyk("halykbank", R.drawable.ioka_ic_bank_halyk),
    HomeCredit("homecredit", R.drawable.ioka_ic_bank_homecredit),
    Jusan("jysan", R.drawable.ioka_ic_bank_jusan),
    Kaspi("kaspibank", R.drawable.ioka_ic_bank_kaspi),
    Nurbank("nurbank", R.drawable.ioka_ic_bank_nurbank),
    Post("kazpost", R.drawable.ioka_ic_bank_post),
    Rbk("bankrbk", R.drawable.ioka_ic_bank_rbk),
    Sber("sberbank", R.drawable.ioka_ic_bank_sber),
    Vtb("vtbbank", R.drawable.ioka_ic_bank_vtb),
    Unknown("unknown", null);

}