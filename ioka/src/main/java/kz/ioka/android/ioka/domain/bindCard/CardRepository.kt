package kz.ioka.android.ioka.domain.bindCard

import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.card.BindCardRequestDto
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.errorHandler.safeApiCall
import kz.ioka.android.ioka.util.getCustomerId

internal interface CardRepository {

    suspend fun bindCard(
        customerToken: String,
        apiKey: String,
        cardPan: String,
        expireDate: String,
        cvv: String
    ): ResultWrapper<CardBindingResultModel>

    suspend fun getCardBindingStatus(
        customerToken: String,
        apiKey: String,
        cardId: String
    ): ResultWrapper<CardBindingStatusModel>

}

internal class CardRepositoryImpl constructor(
    private val cardApi: CardApi
) : CardRepository {

    override suspend fun bindCard(
        customerToken: String,
        apiKey: String,
        cardPan: String,
        expireDate: String,
        cvv: String
    ): ResultWrapper<CardBindingResultModel> {
        return safeApiCall(Dispatchers.IO) {
            val bindCardResult = cardApi.bindCard(
                customerToken.getCustomerId(),
                customerToken,
                apiKey,
                BindCardRequestDto(cardPan, expireDate, cvv)
            )

            when (bindCardResult.status) {
                CardBindingResultModel.STATUS_APPROVED -> CardBindingResultModel.Success
                CardBindingResultModel.STATUS_DECLINED -> CardBindingResultModel.Declined(
                    bindCardResult.error.message
                )
                else -> CardBindingResultModel.Pending(bindCardResult.id, bindCardResult.action.url)
            }
        }
    }

    override suspend fun getCardBindingStatus(
        customerToken: String,
        apiKey: String,
        cardId: String
    ): ResultWrapper<CardBindingStatusModel> {
        return safeApiCall(Dispatchers.IO) {
            val card =
                cardApi.getCardById(apiKey, customerToken, customerToken.getCustomerId(), cardId)

            when (card.status) {
                CardBindingStatusModel.STATUS_APPROVED -> CardBindingStatusModel.Success
                else -> CardBindingStatusModel.Failed(card.error?.message)
            }
        }
    }

}