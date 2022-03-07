package kz.ioka.android.iokademoapp.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

class ProfileDao @Inject constructor() {

    private var customerToken: MutableStateFlow<String?> = MutableStateFlow(null)

    fun setCustomerToken(token: String) {
        customerToken.value = token
    }

    fun getCustomerToken(): String {
        return customerToken.value ?: ""
    }

    fun getCustomerId(): String {
        val customerToken = customerToken.value

        return customerToken?.let {
            val customerIdEndIndex = it.indexOf("_secret")

            it.substring(0, customerIdEndIndex)
        } ?: ""
    }

}