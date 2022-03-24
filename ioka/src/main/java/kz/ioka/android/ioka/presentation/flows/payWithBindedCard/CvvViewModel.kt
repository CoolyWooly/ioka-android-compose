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
    private val launcher: CvvLauncher,
    private val repository: PaymentRepository
) : ViewModel() {

    private var paymentId: String = ""

    private val _payState = MutableLiveData<PayState>(PayState.DISABLED)
    val payState = _payState as LiveData<PayState>

    fun onContinueClicked(cvv: String) {
        viewModelScope.launch {
            _payState.value = PayState.LOADING

            val cardPayment = repository.createPaymentWithCardId(
                launcher.orderToken.getOrderId(),
                launcher.customerToken,
                Config.apiKey!!,
                launcher.cardId,
                cvv
            )

            when (cardPayment) {
                is ResultWrapper.GenericError -> {
                    _payState.postValue(PayState.ERROR)
                }
                is ResultWrapper.NetworkError -> {
                    _payState.postValue(PayState.ERROR)
                }
                is ResultWrapper.Success -> {
                    processSuccessfulResponse(cardPayment.value)
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
            is PaymentModel.Declined -> _payState.postValue(PayState.ERROR)
            else -> _payState.postValue(PayState.SUCCESS)
        }
    }

    fun on3DSecurePassed() {
        viewModelScope.launch {
            _payState.postValue(PayState.LOADING)

            val cardPayment = repository.isPaymentSuccessful(
                Config.apiKey!!,
                launcher.customerToken,
                launcher.orderToken,
                paymentId
            )

            when (cardPayment) {
                is ResultWrapper.GenericError -> {
                    _payState.postValue(PayState.ERROR)
                }
                is ResultWrapper.NetworkError -> {
                    _payState.postValue(PayState.ERROR)
                }
                is ResultWrapper.Success -> {
                    if (cardPayment.value) _payState.postValue(PayState.SUCCESS)
                    else _payState.postValue(PayState.ERROR)
                }
            }
        }
    }

    fun onCvvChanged(newValue: String) {
        _payState.value = if (newValue.length == 3) PayState.DEFAULT else PayState.DISABLED
    }

}