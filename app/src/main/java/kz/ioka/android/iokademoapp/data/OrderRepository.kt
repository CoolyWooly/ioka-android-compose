package kz.ioka.android.iokademoapp.data

import kz.ioka.android.iokademoapp.data.local.ProfileDao
import kz.ioka.android.iokademoapp.data.remote.CheckoutRequestDto
import kz.ioka.android.iokademoapp.data.remote.CheckoutResponseDto
import kz.ioka.android.iokademoapp.data.remote.DemoApi
import javax.inject.Inject

interface OrderRepository {

    suspend fun checkout(price: Int): CheckoutResponseDto

}

class OrderRepositoryImpl @Inject constructor(
    private val demoApi: DemoApi,
    private val profileDao: ProfileDao
) : OrderRepository {

    override suspend fun checkout(price: Int): CheckoutResponseDto {
        val response = demoApi.checkout(CheckoutRequestDto(price))

        response.customerToken?.let {
            profileDao.setCustomerToken(it)
        }

        return response
    }

}