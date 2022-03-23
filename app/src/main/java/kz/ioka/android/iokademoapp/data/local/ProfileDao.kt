package kz.ioka.android.iokademoapp.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ProfileDao @Inject constructor() {

    private var customerToken: MutableStateFlow<String?> = MutableStateFlow(null)

    fun setCustomerToken(token: String) {
        customerToken.value = token
    }

    fun getCustomerToken(): String {
        return customerToken.value ?: ""
    }

}