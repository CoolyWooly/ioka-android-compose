package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.cardInfo.CardBrandModel
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.util.getOrderId

@Suppress("UNCHECKED_CAST")
internal class CvvViewModelFactory(
    val launcher: PayWithCvvLauncher,
    private val repository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CvvViewModel(launcher, repository) as T
    }
}


internal class CvvViewModel(
    launcher: PayWithCvvLauncher,
    private val repository: PaymentRepository
) : ViewModel() {

    val orderToken = launcher.orderToken
    val order = launcher.order
    var cardId: String = launcher.cardId
    var cardNumber: String = launcher.cardNumber
    var cardType: CardBrandModel = CardBrandModel.getByCode(launcher.cardType)

    var paymentId: String = ""

    private val _payState = MutableLiveData<PaymentState>(PaymentState.DISABLED)
    val payState = _payState as LiveData<PaymentState>

    fun onContinueClicked(cvv: String) {
        viewModelScope.launch {
            _payState.value = PaymentState.LOADING

            val cardPayment = repository.createPaymentWithCardId(
                orderToken.getOrderId(),
                Config.apiKey,
                cardId,
                cvv
            )

            when (cardPayment) {
                is ResultWrapper.Success -> {
                    processSuccessfulResponse(cardPayment.value)
                }
                is ResultWrapper.IokaError -> {
                    _payState.postValue(PaymentState.FAILED(cardPayment.message))
                }
                else -> {
                    _payState.postValue(PaymentState.ERROR())
                }
            }
        }
    }

    private fun processSuccessfulResponse(cardPayment: PaymentModel) {
        when (cardPayment) {
            is PaymentModel.Pending -> {
                paymentId = cardPayment.paymentId
                _payState.postValue(PaymentState.PENDING(cardPayment.actionUrl))
            }
            is PaymentModel.Declined -> _payState.postValue(PaymentState.FAILED(cardPayment.message))
            else -> _payState.postValue(PaymentState.SUCCESS)
        }
    }

    fun onCvvChanged(newValue: String) {
        _payState.value =
            if (newValue.length in 3..4) PaymentState.DEFAULT else PaymentState.DISABLED
    }

}