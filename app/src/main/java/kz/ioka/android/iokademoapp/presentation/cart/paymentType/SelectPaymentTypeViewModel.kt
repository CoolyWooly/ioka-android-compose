package kz.ioka.android.iokademoapp.presentation.cart.paymentType

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.api.Ioka
import kz.ioka.android.ioka.api.CardModel
import kz.ioka.android.iokademoapp.common.Optional
import kz.ioka.android.iokademoapp.data.CustomerRepository
import kz.ioka.android.iokademoapp.presentation.cart.PaymentTypeDvo
import javax.inject.Inject

@HiltViewModel
class SelectPaymentTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    customerRepository: CustomerRepository
) : ViewModel() {

    private val _selectedPaymentType = MutableLiveData<Optional<PaymentTypeDvo>>()
    val selectedPaymentType = _selectedPaymentType as LiveData<Optional<PaymentTypeDvo>>

    private val _bindedCards = MutableLiveData<List<CardModel>>(emptyList())
    val bindedCards = _bindedCards as LiveData<List<CardModel>>

    init {
        _selectedPaymentType.value =
            Optional.of(savedStateHandle.get<PaymentTypeDvo>(SelectPaymentTypeActivity.LAUNCHER))

        viewModelScope.launch(Dispatchers.IO) {
            val customerToken = customerRepository.getCustomerToken()

            _bindedCards.postValue(Ioka.getCards(customerToken))
        }
    }

    fun onPaymentTypeSelected(paymentType: PaymentTypeDvo) {
        _selectedPaymentType.value = Optional.of(paymentType)
    }

}