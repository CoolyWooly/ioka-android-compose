package kz.ioka.android.iokademoapp.cart.paymentType

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.util.Optional
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.cart.PaymentTypeDvo
import kz.ioka.android.iokademoapp.data.CardsRepository
import kz.ioka.android.iokademoapp.profile.savedCards.CardDvo
import javax.inject.Inject

@HiltViewModel
class SelectPaymentTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    cardsRepository: CardsRepository
) : ViewModel() {

    private val _selectedPaymentType = MutableLiveData<Optional<PaymentTypeDvo>>()
    val selectedPaymentType = _selectedPaymentType as LiveData<Optional<PaymentTypeDvo>>

    private val _bindedCards = MutableLiveData<List<CardDvo>>(emptyList())
    val bindedCards = _bindedCards as LiveData<List<CardDvo>>

    init {
        _selectedPaymentType.value =
            Optional.of(savedStateHandle.get<PaymentTypeDvo>(SelectPaymentTypeActivity.LAUNCHER))

        viewModelScope.launch(Dispatchers.IO) {
            val savedCards = cardsRepository.getSavedCards().map {
                CardDvo(
                    id = it.id ?: "",
                    cardType = R.drawable.ic_ps_visa,
                    cardPan = it.pan_masked ?: "",
                )
            }

            _bindedCards.postValue(savedCards)
        }
    }

    fun onPaymentTypeSelected(paymentType: PaymentTypeDvo) {
        _selectedPaymentType.value = Optional.of(paymentType)
    }

}