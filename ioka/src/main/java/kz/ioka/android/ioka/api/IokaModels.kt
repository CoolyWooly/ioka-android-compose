package kz.ioka.android.ioka.api

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

/**
 * Класс сохраненной банковской карты
 * [id] - идентификатор сохраненной карты
 * [customerId] - идентификатор пользователя, к которому "привязана" карта
 * [createdAt] - время создания сохраненной карты
 * [panMasked] - маскированный PAN карты в формате "123456******1234"
 * [expiryDate] - месяц и год истечения срока действия карты
 * [holder] - имя держателя карты
 * [paymentSystem] - платежная система карты (VISA, MASTERCARD, и тд.)
 * [emitter] - банк-эмиттент, выпустивший карту
 * [cvcRequired] - необходимость ввода cvc при оплате
 */
@Parcelize
data class CardModel(
    val id: String?,
    val customerId: String?,
    val createdAt: String?,
    val panMasked: String?,
    val expiryDate: String?,
    val holder: String?,
    val paymentSystem: CardBrandModel,
    val emitter: CardEmitterModel,
    val cvcRequired: Boolean?,
) : Parcelable

/**
 * Enum-класс платежной системы банковской карты (Visa, MasterCard, и т.д.)
 * [code] - код платежной системы, передаваемый бэкендом ioka
 * [iconRes] - иконка платежной системы из ресурсов, помечена аннотацией [DrawableRes]
 */
@Parcelize
enum class CardBrandModel(
    val code: String,
    @DrawableRes val iconRes: Int,
) : Parcelable {

    Amex("AMERICAN_EXPRESS", R.drawable.ioka_ic_ps_amex),
    DinerClub("DINER_CLUB", R.drawable.ioka_ic_ps_dinersclub),
    Maestro("MAESTRO", R.drawable.ioka_ic_ps_maestro),
    MasterCard("MASTERCARD", R.drawable.ioka_ic_ps_mastercard),
    Mir("MIR", R.drawable.ioka_ic_ps_mir),
    UnionPay("UNION_PAY", R.drawable.ioka_ic_ps_unionpay),
    Visa("VISA", R.drawable.ioka_ic_ps_visa),
    Unknown("UNKNOWN", R.drawable.ioka_ic_ps_unknown);

    companion object {
        fun getByCode(code: String?): CardBrandModel {
            return values().find { it.code == code } ?: Unknown
        }
    }

}

/**
 * Enum-класс банка-эмиттента кредитной карты (HalykBank, KaspiBank, и т.д.)
 * [code] - код банка-эмиттента, передаваемый бэкендом ioka
 * [iconRes] - иконка банка-эмиттента из ресурсов, помечена аннотацией [DrawableRes]
 */
@Parcelize
enum class CardEmitterModel(
    val code: String,
    @DrawableRes val iconRes: Int?,
) : Parcelable {

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
        fun getByCode(code: String?): CardEmitterModel {
            return values().find { it.code == code } ?: Unknown
        }
    }

}

@Parcelize
data class CardDvo(
    val cardId: String,
    val cardNumber: String,
    val cardType: String,
    val cvvRequired: Boolean,
) : Parcelable