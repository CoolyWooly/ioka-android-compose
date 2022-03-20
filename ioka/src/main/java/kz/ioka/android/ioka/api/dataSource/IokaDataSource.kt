package kz.ioka.android.ioka.api.dataSource

import kz.ioka.android.ioka.Config.apiKey
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.util.getCustomerId

interface IokaDataSource {

    suspend fun getCards(customerToken: String): List<CardModel>

    suspend fun removeCard(customerToken: String, cardId: String): Boolean

}

class IokaDataSourceImpl : IokaDataSource {

    private val cardApi = DependencyInjector.cardApi

    override suspend fun getCards(customerToken: String): List<CardModel> {
        if (apiKey == null) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return cardApi.getCards(
            apiKey!!, customerToken, customerToken.getCustomerId()
        ).map {
            CardModel(
                id = it.id,
                customerId = it.customer_id,
                createdAt = it.created_at,
                panMasked = it.pan_masked,
                expiryDate = it.expiry_date,
                holder = it.holder,
                paymentSystem = it.payment_system,
                emitter = it.emitter,
                cvcRequired = it.cvc_required,
            )
        }
    }

    override suspend fun removeCard(
        customerToken: String,
        cardId: String
    ): Boolean {
        if (apiKey == null) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        val request =
            cardApi.removeCard(apiKey!!, customerToken, customerToken.getCustomerId(), cardId)
                .execute()

        return request.isSuccessful
    }

}