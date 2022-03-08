package kz.ioka.android.ioka.domain.bindCard

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.card.BindCardRequestDto
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.domain.common.ResultWrapper
import kz.ioka.android.ioka.domain.common.safeApiCall
import kz.ioka.android.ioka.util.getCustomerId
import javax.inject.Inject

interface CardRepository {

    suspend fun saveCard(
        customerToken: String,
        apiKey: String,
        cardPan: String,
        expireDate: String,
        cvv: String
    ): ResultWrapper<CardBindingResultModel>

}

class CardRepositoryImpl @Inject constructor(
    private val cardApi: CardApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CardRepository {

    override suspend fun saveCard(
        customerToken: String,
        apiKey: String,
        cardPan: String,
        expireDate: String,
        cvv: String
    ): ResultWrapper<CardBindingResultModel> {
        return safeApiCall(dispatcher) {
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
                else -> CardBindingResultModel.Pending(bindCardResult.action.url)
            }
        }
    }

}