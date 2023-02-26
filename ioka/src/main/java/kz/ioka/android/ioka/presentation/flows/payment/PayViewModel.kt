package kz.ioka.android.ioka.presentation.flows.payment

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.util.getOrderId

@Suppress("UNCHECKED_CAST")
internal class PayWithCardViewModelFactory(
    val launcher: PaymentFormLauncher,
    private val paymentRepository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PayWithCardViewModel(launcher, paymentRepository) as T
    }
}

internal class PayWithCardViewModel constructor(
    launcher: PaymentFormLauncher,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    val orderToken = launcher.orderToken
    val order = launcher.order
    val withGooglePay = launcher.withGooglePay
    val canSaveCard = launcher.canSaveCard

    var paymentId: String = ""

    private val _payState = MutableLiveData<PaymentState>(PaymentState.DEFAULT)
    val payState = _payState as LiveData<PaymentState>

    private val _isPayAvailable = MutableLiveData(false)
    val isPayAvailable = _isPayAvailable as LiveData<Boolean>

    private var isCardNumberValid = false
    private var isExpiryDateValid = false
    private var isCvvValid = false

    fun onPayClicked(cardPan: String, expireDate: String, cvv: String, saveCard: Boolean) {
        viewModelScope.launch {
            val areAllFieldsValid = _isPayAvailable.value

            if (areAllFieldsValid == true) {
                _payState.postValue(PaymentState.LOADING)
                _isPayAvailable.postValue(false)

                val cardPayment = paymentRepository.createCardPayment(
                    orderToken.getOrderId(),
                    Config.apiKey,
                    cardPan, expireDate, cvv, saveCard
                )

                _isPayAvailable.postValue(true)

                when (cardPayment) {
                    is ResultWrapper.Success -> {
                        processSuccessfulResponse(cardPayment.value)
                    }
                    is ResultWrapper.IokaError -> {
                        _payState.postValue(PaymentState.FAILED(cardPayment.message))
                    }
                    is ResultWrapper.NetworkError -> {
                        _payState.postValue(PaymentState.ERROR("NetworkError"))
                    }
                    is ResultWrapper.HttpError -> {
                        _payState.postValue(PaymentState.ERROR("HttpError"))
                    }
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

    fun setIsCardNumberValid(isValid: Boolean) {
        isCardNumberValid = isValid

        _isPayAvailable.value = isCardNumberValid && isExpiryDateValid && isCvvValid
    }

    fun setIsExpiryDateValid(isValid: Boolean) {
        isExpiryDateValid = isValid

        _isPayAvailable.value = isCardNumberValid && isExpiryDateValid && isCvvValid
    }

    fun setIsCvvValid(isValid: Boolean) {
        isCvvValid = isValid

        _isPayAvailable.value = isCardNumberValid && isExpiryDateValid && isCvvValid
    }

}