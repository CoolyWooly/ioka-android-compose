package kz.ioka.android.iokademoapp.data

import kz.ioka.android.iokademoapp.BuildConfig
import kz.ioka.android.iokademoapp.data.local.ProfileDao
import kz.ioka.android.iokademoapp.data.remote.CardsApi
import kz.ioka.android.iokademoapp.data.remote.CardsResultDto
import java.net.ProtocolException
import javax.inject.Inject

interface CardsRepository {

    suspend fun getSavedCards(): List<CardsResultDto>
    suspend fun removeCard(cardId: String)

}

class CardsRepositoryImpl @Inject constructor(
    private val cardsApi: CardsApi,
    private val profileDao: ProfileDao
) : CardsRepository {

    override suspend fun getSavedCards(): List<CardsResultDto> {
        val customerId = profileDao.getCustomerId()
        val customerToken = profileDao.getCustomerToken()

        return cardsApi.getCards(customerId, BuildConfig.API_KEY, customerToken)
    }

    override suspend fun removeCard(cardId: String) {
        val customerId = profileDao.getCustomerId()
        val customerToken = profileDao.getCustomerToken()

        try {
            cardsApi.removeCard(customerId, cardId, BuildConfig.API_KEY, customerToken)
        } catch (e: ProtocolException) {
        }
    }

}