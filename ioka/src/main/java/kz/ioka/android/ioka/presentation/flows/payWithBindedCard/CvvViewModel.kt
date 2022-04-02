package kz.ioka.android.ioka.presentation.flows.payWithBindedCard

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.common.ResultWrapper
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayState
import kz.ioka.android.ioka.util.getOrderId

@Suppress("UNCHECKED_CAST")
internal class CvvViewModelFactory(
    val launcher: CvvLauncher,
    private val repository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CvvViewModel(launcher, repository) as T
    }
}


internal class CvvViewModel(
    launcher: CvvLauncher,
    private val repository: PaymentRepository
) : ViewModel() {

    val price = launcher.price
    val customerToken = launcher.customerToken
    val orderToken = launcher.orderToken
    var paymentId: String = ""
    var cardId: String = launcher.cardId

    private val _payState = MutableLiveData<PayState>(PayState.DISABLED)
    val payState = _payState as LiveData<PayState>

    fun onContinueClicked(cvv: String) {
        viewModelScope.launch {
            _payState.value = PayState.LOADING

            val cardPayment = repository.createPaymentWithCardId(
                orderToken.getOrderId(),
                customerToken,
                Config.apiKey,
                cardId,
                cvv
            )

            when (cardPayment) {
                is ResultWrapper.Success -> {
                    processSuccessfulResponse(cardPayment.value)
                }
                is ResultWrapper.IokaError -> {
                    _payState.postValue(PayState.FAILED(cardPayment.message))
                }
                else -> {
                    _payState.postValue(PayState.ERROR())
                }
            }
        }
    }

    private fun processSuccessfulResponse(cardPayment: PaymentModel) {
        when (cardPayment) {
            is PaymentModel.Pending -> {
                paymentId = cardPayment.paymentId
                _payState.postValue(PayState.PENDING(cardPayment.actionUrl))
            }
            is PaymentModel.Declined -> _payState.postValue(PayState.FAILED(cardPayment.message))
            else -> _payState.postValue(PayState.SUCCESS)
        }
    }

    fun onCvvChanged(newValue: String) {
        _payState.value = if (newValue.length == 3) PayState.DEFAULT else PayState.DISABLED
    }

}