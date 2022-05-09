package kz.ioka.android.ioka.domain.order

import kotlinx.coroutines.Dispatchers
import kz.ioka.android.ioka.data.order.OrderApi
import kz.ioka.android.ioka.domain.common.Currency
import kz.ioka.android.ioka.domain.common.toCurrency
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.errorHandler.safeApiCall
import kz.ioka.android.ioka.util.toAmount

internal interface OrderRepository {

    suspend fun getOrderById(orderId: String): ResultWrapper<OrderModel>

}

internal class OrderRepositoryImpl(
    private val orderApi: OrderApi
) : OrderRepository {

    override suspend fun getOrderById(orderId: String): ResultWrapper<OrderModel> {
        return safeApiCall(Dispatchers.IO) {
            val orderResponse = orderApi.getOrderById(orderId)

            val amount = (orderResponse.amount ?: 0).toAmount(
                orderResponse.currency?.toCurrency() ?: Currency.KZT
            )

            OrderModel(
                orderResponse.external_id ?: "",
                orderResponse.customer_id,
                amount
            )
        }
    }

}