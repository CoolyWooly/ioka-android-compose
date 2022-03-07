package kz.ioka.android.iokademoapp.data

import kz.ioka.android.iokademoapp.data.local.ProfileDao
import javax.inject.Inject

interface CustomerRepository {

    suspend fun getCustomerToken(): String

}

class CustomerRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : CustomerRepository {

    override suspend fun getCustomerToken(): String {
        return profileDao.getCustomerToken()
    }

}