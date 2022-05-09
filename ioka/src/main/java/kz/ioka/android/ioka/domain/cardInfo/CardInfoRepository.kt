package kz.ioka.android.ioka.domain.cardInfo

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.errorHandler.safeApiCall

internal interface CardInfoRepository {

    suspend fun getBrand(partialCardBin: String): ResultWrapper<CardBrandModel>
    suspend fun getEmitter(cardBin: String): ResultWrapper<CardEmitterModel>

}

internal class CardInfoRepositoryImpl constructor(
    private val cardInfoApi: CardInfoApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CardInfoRepository {

    override suspend fun getBrand(partialCardBin: String): ResultWrapper<CardBrandModel> {
        return safeApiCall(dispatcher) {
            val response = cardInfoApi.getBrand(partialCardBin)

            CardBrandModel.getByCode(response.brand)
        }
    }

    override suspend fun getEmitter(cardBin: String): ResultWrapper<CardEmitterModel> {
        return safeApiCall(dispatcher) {
            val response = cardInfoApi.getEmitter(cardBin)

            CardEmitterModel.getByCode(response.emitterCode)
        }
    }
}