package kz.ioka.android.ioka.domain.common

import android.text.TextUtils
import java.io.Serializable

enum class Currency(
    val code: Int,
    val shortName: String,
    val symbol: String = shortName,
) : Serializable {
    UNKNOWN(-1, ""),
    RUR(810, "RUR", 8381),
    GBP(826, "GBP", 163),
    CHF(756, "CHF", 8355),
    JPY(392, "JPY", 165),
    UAH(980, "UAH", 8372),
    KZT(398, "KZT", 8376),
    CNY(156, "CNY", 20803),
    BYR(974, "BYR"),
    BYN(933, "BYN"),
    CAD(124, "CAD"),
    NZD(554, "NZD"),
    DKK(208, "DKK"),
    NOK(578, "NOK"),
    SEK(752, "SEK"),
    MXN(484, "MXN"),
    IRR(364, "IRR"),
    GEL(981, "GEL"),
    AMD(51, "AMD"),
    THB(764, "THB"),
    INR(356, "INR"),
    AZN(944, "AZN"),
    MYR(458, "MYR"),
    BRL(986, "BRL"),
    HKD(344, "HKD"),
    TJS(972, "TJS"),
    CZK(203, "CZK"),
    HUF(348, "HUF"),
    TRY(949, "TRY"),
    ZAR(710, "ZAR"),
    PLN(985, "PLN"),
    KRW(410, "KRW"),
    EEK(233, "EEK"),
    UZS(860, "UZS"),
    TRL(792, "TRL"),
    SGD(702, "SGD"),
    XDR(960, "XDR"),
    SAR(682, "SAR"),
    MDL(498, "MDL"),
    LVL(428, "LVL"),
    KGS(417, "KGS"),
    KWD(414, "KWD"),
    AED(784, "AED"),
    AUD(36, "AUD"),
    DEM(41, "DEM"),
    RUB(643, "RUB", 8381),
    EUR(978, "EUR", 8364),
    USD(840, "USD", 36);

    val displaySymbol by lazy { if (TextUtils.isEmpty(this.symbol)) this.shortName else this.symbol }

    private constructor(code: Int, shortName: String, symbolHex: Int) : this(
        code,
        shortName,
        String(Character.toChars(symbolHex))
    ) {
    }

    companion object {

        fun fromCode(code: Int): Currency {
            val var1 = values()
            val var2 = var1.size

            for (var3 in 0 until var2) {
                val curr = var1[var3]
                if (curr.code == code) {
                    return curr
                }
            }

            return UNKNOWN
        }

        fun fromShortName(shortName: String): Currency {
            val var1 = values()
            val var2 = var1.size

            for (var3 in 0 until var2) {
                val curr = var1[var3]
                if (curr.shortName.equals(shortName, ignoreCase = true)) {
                    return curr
                }
            }

            return UNKNOWN
        }
    }
}

fun String.toCurrency(): Currency {
    return Currency.fromShortName(this)
}