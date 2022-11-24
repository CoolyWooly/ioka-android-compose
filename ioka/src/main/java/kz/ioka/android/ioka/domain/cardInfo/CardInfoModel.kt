package kz.ioka.android.ioka.domain.cardInfo

import androidx.annotation.DrawableRes
import kz.ioka.android.ioka.R

internal enum class CardBrandModel(
    val code: String,
    @DrawableRes val iconRes: Int,
    val cardNumberLength: IntRange
) {

    Amex("AMERICAN_EXPRESS", R.drawable.ioka_ic_ps_amex, 15..15),
    DinerClub("DINER_CLUB", R.drawable.ioka_ic_ps_dinersclub, 16..16),
    Maestro("MAESTRO", R.drawable.ioka_ic_ps_maestro, 12..19),
    MasterCard("MASTERCARD", R.drawable.ioka_ic_ps_mastercard, 16..16),
    Mir("MIR", R.drawable.ioka_ic_ps_mir, 16..19),
    UnionPay("UNION_PAY", R.drawable.ioka_ic_ps_unionpay, 16..19),
    Visa("VISA", R.drawable.ioka_ic_ps_visa, 16..16),
    Unknown("UNKNOWN", R.drawable.ioka_ic_ps_unknown, 16..16);

    companion object {
        fun getByCode(code: String): CardBrandModel {
            return values().find { it.code == code } ?: Unknown
        }
    }

}

internal enum class CardEmitterModel(
    val code: String,
    @DrawableRes val iconRes: Int?,
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

    companion object {
        fun getByCode(code: String): CardEmitterModel {
            return values().find { it.code == code } ?: Unknown
        }
    }

}