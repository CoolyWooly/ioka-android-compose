package kz.ioka.android.ioka.api.dataSource

import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.Config.apiKey
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.common.ResultWrapper
import kz.ioka.android.ioka.domain.common.safeApiCall
import kz.ioka.android.ioka.util.getCustomerId
import java.lang.Exception
import java.net.ProtocolException

interface IokaDataSource {

    suspend fun getCards(customerToken: String): List<CardModel>

    suspend fun removeCard(customerToken: String, cardId: String): Boolean

}

class IokaDataSourceImpl : IokaDataSource {

    private val cardApi = DependencyInjector.cardApi

    override suspend fun getCards(customerToken: String): List<CardModel> {
        if (Config.isApiKeyInitialized().not()) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return cardApi.getCards(
            apiKey, customerToken, customerToken.getCustomerId()
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
        if (Config.isApiKeyInitialized().not()) {
            throw RuntimeException("Init Ioka with your API_KEY")
        }

        return try {
            cardApi.removeCard(
                apiKey,
                customerToken,
                customerToken.getCustomerId(),
                cardId
            )
            true
        } catch (e: Exception) {
            e is ProtocolException
        }
    }

}