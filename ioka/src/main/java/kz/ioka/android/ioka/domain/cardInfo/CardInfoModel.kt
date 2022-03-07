package kz.ioka.android.ioka.domain.cardInfo

sealed interface CardBrandModel {

    object Visa : CardBrandModel
    object MasterCard : CardBrandModel
    object Unknown : CardBrandModel

}

sealed interface CardEmitterModel {

    object Alfa : CardEmitterModel
    object Kaspi : CardEmitterModel
    object Unknown : CardEmitterModel

}