package kz.ioka.android.ioka.domain.cardInfo

import kz.ioka.android.ioka.data.cardInfo.BrandResponseDto
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.data.cardInfo.EmitterResponseDto
import retrofit2.Response

internal interface CardInfoRepository {

    suspend fun getBrand(partialCardBin: String): CardBrandModel
    suspend fun getEmitter(cardBin: String): CardEmitterModel

}

internal class CardInfoRepositoryImpl constructor(
    private val cardInfoApi: CardInfoApi
) : CardInfoRepository {

    override suspend fun getBrand(partialCardBin: String): CardBrandModel {
        val response: Response<BrandResponseDto>

        try {
            response = cardInfoApi.getBrand(partialCardBin)
        } catch (t: Throwable) {
            return CardBrandModel.Unknown
        }

        return if (!response.isSuccessful) {
            CardBrandModel.Unknown
        } else {
            CardBrandModel.values().find {
                it.code == response.body()?.brand
            } ?: CardBrandModel.Unknown
        }
    }

    override suspend fun getEmitter(cardBin: String): CardEmitterModel {
        val response: Response<EmitterResponseDto>

        try {
            response = cardInfoApi.getEmitter(cardBin)
        } catch (t: Throwable) {
            return CardEmitterModel.Unknown
        }

        return if (!response.isSuccessful) {
            CardEmitterModel.Unknown
        } else {
            val emitter = response.body()

            return CardEmitterModel.values().find {
                it.name == emitter?.emitterCode
            } ?: CardEmitterModel.Unknown
        }
    }
}