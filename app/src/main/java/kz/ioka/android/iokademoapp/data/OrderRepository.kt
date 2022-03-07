package kz.ioka.android.iokademoapp.data

import kz.ioka.android.iokademoapp.data.remote.CheckoutRequestDto
import kz.ioka.android.iokademoapp.data.remote.CheckoutResponseDto
import kz.ioka.android.iokademoapp.data.remote.DemoApi
import javax.inject.Inject

interface OrderRepository {

    suspend fun checkout(price: Int): CheckoutResponseDto

}

class OrderRepositoryImpl @Inject constructor(
    private val demoApi: DemoApi
) : OrderRepository {

    override suspend fun checkout(price: Int): CheckoutResponseDto {
        return demoApi.checkout(CheckoutRequestDto(price))
    }

}