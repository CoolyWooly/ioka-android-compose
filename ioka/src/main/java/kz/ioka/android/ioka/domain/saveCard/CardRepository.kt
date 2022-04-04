package kz.ioka.android.ioka.domain.saveCard

import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.card.SaveCardRequestDto
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.errorHandler.safeApiCall
import kz.ioka.android.ioka.util.getCustomerId

internal interface CardRepository {

    suspend fun saveCard(
        customerToken: String,
        apiKey: String,
        cardPan: String,
        expireDate: String,
        cvv: String
    ): ResultWrapper<SaveCardResultModel>

    suspend fun getSaveCardStatus(
        customerToken: String,
        apiKey: String,
        cardId: String
    ): ResultWrapper<SaveCardStatusModel>

}

internal class CardRepositoryImpl constructor(
    private val cardApi: CardApi
) : CardRepository {

    override suspend fun saveCard(
        customerToken: String,
        apiKey: String,
        cardPan: String,
        expireDate: String,
        cvv: String
    ): ResultWrapper<SaveCardResultModel> {
        return safeApiCall(Dispatchers.IO) {
            val saveCardResult = cardApi.saveCard(
                customerToken.getCustomerId(),
                customerToken,
                apiKey,
                SaveCardRequestDto(cardPan, expireDate, cvv)
            )

            when (saveCardResult.status) {
                SaveCardResultModel.STATUS_APPROVED -> SaveCardResultModel.Success
                SaveCardResultModel.STATUS_DECLINED -> SaveCardResultModel.Declined(
                    saveCardResult.error.message
                )
                else -> SaveCardResultModel.Pending(saveCardResult.id, saveCardResult.action.url)
            }
        }
    }

    override suspend fun getSaveCardStatus(
        customerToken: String,
        apiKey: String,
        cardId: String
    ): ResultWrapper<SaveCardStatusModel> {
        return safeApiCall(Dispatchers.IO) {
            val card =
                cardApi.getCardById(apiKey, customerToken, customerToken.getCustomerId(), cardId)

            when (card.status) {
                SaveCardStatusModel.STATUS_APPROVED -> SaveCardStatusModel.Success
                else -> SaveCardStatusModel.Failed(card.error?.message)
            }
        }
    }

}