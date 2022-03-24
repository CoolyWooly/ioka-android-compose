package kz.ioka.android.ioka.domain.cardInfo

internal sealed interface CardBrandModel {

    object Visa : CardBrandModel
    object MasterCard : CardBrandModel
    object Unknown : CardBrandModel

}

internal sealed interface CardEmitterModel {

    object Alfa : CardEmitterModel
    object Kaspi : CardEmitterModel
    object Unknown : CardEmitterModel

}