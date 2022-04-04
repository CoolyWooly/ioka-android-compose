package kz.ioka.android.ioka.presentation.flows.payment

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.util.getOrderId
import java.util.*

@Suppress("UNCHECKED_CAST")
internal class PayWithCardViewModelFactory(
    val launcher: PayLauncher,
    private val paymentRepository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PayWithCardViewModel(launcher, paymentRepository) as T
    }
}

internal class PayWithCardViewModel constructor(
    launcher: PayLauncher,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    val orderToken = launcher.orderToken
    val order = launcher.order
    val withGooglePay = launcher.withGooglePay
    val canSaveCard = launcher.canSaveCard

    var paymentId: String = ""

    private val _isCardPanValid = MutableStateFlow(false)
    private val _isExpireDateValid = MutableStateFlow(false)
    private val _isCvvValid = MutableStateFlow(false)

    private val allFieldsAreValid: Flow<Boolean> = combine(
        _isCardPanValid,
        _isExpireDateValid,
        _isCvvValid
    ) { isCardPanValid, isExpireDateValid, isCvvValid ->
        isCardPanValid && isExpireDateValid && isCvvValid
    }

    private val _payState = MutableLiveData<PaymentState>(PaymentState.DEFAULT)
    val payState = _payState as LiveData<PaymentState>

    init {
        viewModelScope.launch {
            allFieldsAreValid.collect { areAllFieldsValid ->
                if (areAllFieldsValid) {
                    _payState.postValue(PaymentState.DEFAULT)
                } else {
                    _payState.postValue(PaymentState.DISABLED)
                }
            }
        }
    }

    fun onCardPanEntered(cardPan: String) {
        _isCardPanValid.value = cardPan.length in 15..19
    }

    fun onExpireDateEntered(expireDate: String) {
        _isExpireDateValid.value = if (expireDate.length < 4) {
            false
        } else {
            val month = expireDate.substring(0..1).toInt()
            val year = expireDate.substring(2).toInt()

            val currentTime = Calendar.getInstance()
            val currentMonth = currentTime.get(Calendar.MONTH)
            val currentYear = currentTime.get(Calendar.YEAR) - 2000

            month <= 12 && (year > currentYear || (year == currentYear && month >= currentMonth))
        }
    }

    fun onCvvEntered(cvv: String) {
        _isCvvValid.value = cvv.length in 3..4
    }

    fun onPayClicked(cardPan: String, expireDate: String, cvv: String, saveCard: Boolean) {
        viewModelScope.launch {
            val areAllFieldsValid = allFieldsAreValid.first()

            if (areAllFieldsValid) {
                _payState.postValue(PaymentState.LOADING)

                val cardPayment = paymentRepository.createCardPayment(
                    orderToken.getOrderId(),
                    Config.apiKey,
                    cardPan, expireDate, cvv, saveCard
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

}