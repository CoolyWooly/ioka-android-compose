package kz.ioka.android.ioka.domain.cardInfo

import android.util.Log
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.data.cardInfo.EmitterResponseDto
import retrofit2.Response

interface CardInfoRepository {

    suspend fun getBrand(partialCardBin: String): CardBrandModel
    suspend fun getEmitter(cardBin: String): CardEmitterModel

}

class CardInfoRepositoryImpl constructor(
    private val cardInfoApi: CardInfoApi
) : CardInfoRepository {

    override suspend fun getBrand(partialCardBin: String): CardBrandModel {
        return when (cardInfoApi.getBrand(partialCardBin).brand) {
            "MASTERCARD" -> CardBrandModel.MasterCard
            "VISA" -> CardBrandModel.Visa
            else -> CardBrandModel.Unknown
        }
    }

    override suspend fun getEmitter(cardBin: String): CardEmitterModel {
        val response: Response<EmitterResponseDto>

        try {
            response = cardInfoApi.getEmitter(cardBin)
        } catch (t: Throwable) {
            Log.d("Error catched: ", t.localizedMessage ?: "NULL")
            return CardEmitterModel.Unknown
        }

        return if (!response.isSuccessful) {
            CardEmitterModel.Unknown
        } else {
            val emitter = response.body()

            return if (emitter == null) {
                CardEmitterModel.Unknown
            } else {
                when (emitter.emitterCode) {
                    "alfabank" -> CardEmitterModel.Alfa
                    "kaspibank" -> CardEmitterModel.Kaspi
                    else -> CardEmitterModel.Unknown
                }
            }
        }
    }

}